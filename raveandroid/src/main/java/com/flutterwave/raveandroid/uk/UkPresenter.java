package com.flutterwave.raveandroid.uk;

import android.content.Context;
import android.util.Log;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.RaveConstants.validAmountPrompt;

/**
 * Created by hfetuga on 27/06/2018.
 */


public class UkPresenter implements UkContract.UserActionsListener {

    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    private Context context;
    private UkContract.View mView;

    @Inject
    public UkPresenter(Context context, UkContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("3");
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        networkRequest.getFee(body, new Callbacks.OnGetFeeRequestComplete() {
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
                mView.showFetchFeeFailed(transactionError);
            }
        });
    }

    @Override
    public void chargeUk(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey).trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        networkRequest.chargeUK(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    Log.d("resp", responseAsJSONString);

                    mView.showTransactionPage(response);
                } else {
                    mView.onPaymentError(noResponse);
                }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void requeryTx(final String flwRef, final String txRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mView.showPollingIndicator(true);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    mView.onPollingRoundComplete(flwRef, txRef, publicKey);
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentSuccessful(flwRef, txRef, responseAsJSONString);
                } else {
                    mView.showProgressIndicator(false);
                    mView.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
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
                deviceID = Utils.getDeviceImei(context);
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

            Payload body = builder.createUKPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                chargeUk(body, ravePayInitializer.getEncryptionKey());
            }
        }
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
    public void onAttachView(UkContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullUkView();
    }
}
