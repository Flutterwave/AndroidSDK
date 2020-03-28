package com.flutterwave.raveandroid.barter;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;

public class BarterPresenter implements BarterContract.UserActionsListener {

    @Inject
    RemoteRepository networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private Context context;
    private BarterContract.View mView;

    @Inject
    public BarterPresenter(Context context, BarterContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    public BarterPresenter(Context context, BarterContract.View mView, RaveUiComponent raveUiComponent) {
        this.mView = mView;
        this.context = context;
        this.amountValidator = raveUiComponent.amountValidator();
        this.networkRequest = raveUiComponent.networkImpl();
        this.deviceIdGetter = raveUiComponent.deviceIdGetter();
        this.payloadEncryptor = raveUiComponent.payloadEncryptor();
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            }
        }
    }


    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        if (amountValidator == null) {
            amountValidator = new AmountValidator();
        }

        boolean isAmountValid = amountValidator.isAmountValid(amount);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }


    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

            String deviceID = deviceIdGetter.getDeviceId();
            if (deviceID == null) {
                deviceID = Utils.getDeviceId(context);
            }

            PayloadBuilder builder = new PayloadBuilder();

            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID)
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createBarterPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                chargeBarter(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }


    @Override
    public void chargeBarter(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);
        encryptedCardRequestBody = encryptedCardRequestBody.trim();
        encryptedCardRequestBody = encryptedCardRequestBody.replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    try {
                        Uri requeryUri = Uri.parse(response.getData().getRequery_url());
                        Set<String> args = requeryUri.getQueryParameterNames();
                        Uri redirectUri = Uri.parse(response.getData().getRedirect_url());
                        Uri.Builder authUrlBuilder = new Uri.Builder()
                                .scheme(redirectUri.getScheme())
                                .authority(redirectUri.getAuthority());
                        for (String arg : args) {
                            authUrlBuilder.appendQueryParameter(arg, requeryUri.getQueryParameter(arg));
                        }
                        String authUrlCrude = authUrlBuilder.build().toString();

                        String flwRef = response.getData().getFlw_reference();
                        if (flwRef == null) flwRef = response.getData().getFlwRef();

                        mView.loadBarterCheckout(authUrlCrude, flwRef);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mView.onPaymentError("An error occurred with your payment. Please try again or contact support.");
                    }
                } else {
                    mView.onPaymentError(noResponse);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void requeryTx(final String flwRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setOrder_ref(flwRef); // Uses Order ref instead of flwref
        body.setPBFPubKey(publicKey);

        mView.showPollingIndicator(true);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mView.onPaymentFailed(responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    mView.onPollingRoundComplete(flwRef, publicKey);
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentSuccessful(responseAsJSONString);
                } else {
                    mView.showProgressIndicator(false);
                    mView.onPaymentFailed(responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(responseAsJSONString);
            }
        });
    }


    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload);
                } catch (Exception e) {
                    mView.showFetchFeeFailed(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mView.showFetchFeeFailed(message);
            }
        });
    }


    @Override
    public void onAttachView(BarterContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullBarterView();
    }


}
