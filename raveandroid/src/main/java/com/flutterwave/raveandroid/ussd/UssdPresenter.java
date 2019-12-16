package com.flutterwave.raveandroid.ussd;


import android.content.Context;
import android.util.Log;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.PayloadToJson;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.EventLogger;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.data.events.RequeryEvent;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.RaveConstants.transactionError;

public class UssdPresenter implements UssdContract.UserActionsListener {
    private Context context;
    public UssdContract.View mView;

    public boolean pollingCancelled = false;

    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    NetworkRequestImpl networkRequest;
    private String txRef = null, flwRef = null, publicKey = null, ussdCode = null, referenceCode = null;
    private long requeryCountdownTime = 0;

    @Inject
    UssdPresenter(Context context, UssdContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(RaveConstants.fieldAmount).getData()));

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setAccountbank(RaveConstants.ussdBanksList
                            .get(dataHashMap.get(RaveConstants.fieldUssdBank).getData()))
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceIdGetter.getDeviceId())
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId())
                    .setNarration(ravePayInitializer.getNarration());

            Payload body = builder.createUssdPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                payWithUssd(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }


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
                Log.e(RaveConstants.RAVEPAY, message);
                mView.showFetchFeeFailed(transactionError);
            }
        });
    }

    @Override
    public void payWithUssd(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = payloadToJson.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);
        encryptedCardRequestBody = encryptedCardRequestBody.trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("USSD").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    Log.d("resp", responseAsJSONString);

                    flwRef = response.getData().getUssdData().getFlw_reference();
                    publicKey = payload.getPBFPubKey();
                    String note = null;
                    if (response.getData().getNote() != null) note = response.getData().getNote();
                    else if (response.getData().getUssdData().getNote() != null)
                        note = response.getData().getUssdData().getNote();
                    else mView.onPaymentError("No response data was returned");
                    if (note != null) {
                        if (note.contains("|")) {
                            ussdCode = note.substring(0, note.indexOf("|"));
                        } else ussdCode = note;
                        referenceCode = response.getData().getUssdData().getReference_code();
                        mView.onUssdDetailsReceived(ussdCode, referenceCode);
                    }
                } else {
                    mView.onPaymentError("No response data was returned");
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
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("USSD Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            } else mView.onAmountValidationFailed();

        }
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {
        boolean valid = true;

        int amountID = dataHashMap.get(RaveConstants.fieldAmount).getViewId();
        String amount = dataHashMap.get(RaveConstants.fieldAmount).getData();
        Class amountViewType = dataHashMap.get(RaveConstants.fieldAmount).getViewType();

        int bankID = dataHashMap.get(RaveConstants.fieldUssdBank).getViewId();
        String bankName = dataHashMap.get(RaveConstants.fieldUssdBank).getData();
        Class bankViewType = dataHashMap.get(RaveConstants.fieldUssdBank).getViewType();


        if (!amountValidator.isAmountValid(amount)) {
            valid = false;
            mView.showFieldError(amountID, RaveConstants.validAmountPrompt, amountViewType);
        }

        if (bankName == null || !RaveConstants.ussdBanksList.containsKey(bankName)) {
            valid = false;
            mView.showFieldError(bankID, "Please select a bank", bankViewType);
        }

        if (valid) {
            mView.onDataValidationSuccessful(dataHashMap);
        }
    }

    @Override
    public void startPaymentVerification() {
        requeryCountdownTime = System.currentTimeMillis();
        mView.showPollingIndicator(true);
        requeryTx(flwRef, publicKey, requeryCountdownTime);
    }

    public void requeryTx(final String flwRef, final String publicKey, final long requeryCountdownTime) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {

            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {

                if (response.getData() == null) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    if (pollingCancelled) {
                        mView.showPollingIndicator(false);
                        mView.onPollingCanceled(flwRef, responseAsJSONString);
                    } else {
                        if ((System.currentTimeMillis() - requeryCountdownTime) < 300000) {
                            requeryTx(flwRef, publicKey, requeryCountdownTime);
                        } else {
                            mView.showPollingIndicator(false);
                            mView.onPollingTimeout(flwRef, responseAsJSONString);
                        }
                    }
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentSuccessful(flwRef, responseAsJSONString);
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
    public void cancelPolling() {
        pollingCancelled = true;
    }

    @Override
    public void onAttachView(UssdContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullUssdView();
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        eventLogger.logEvent(event, publicKey);
    }
}