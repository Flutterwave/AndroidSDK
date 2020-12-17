package com.flutterwave.raveandroid.rave_presentation.ussd;


import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

public class UssdHandler implements UssdContract.Handler {
    public UssdContract.Interactor mInteractor;

    public boolean pollingCancelled = false;

    @Inject
    EventLogger eventLogger;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    RemoteRepository networkRequest;
    private String txRef = null, flwRef = null, publicKey = null, ussdCode = null, referenceCode = null;
    private long requeryCountdownTime = 0;

    @Inject
    public UssdHandler(UssdContract.Interactor mInteractor) {
        this.mInteractor = mInteractor;
    }


    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("3");
        body.setPBFPubKey(payload.getPBFPubKey());

        mInteractor.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mInteractor.showProgressIndicator(false);

                try {
                    mInteractor.onTransactionFeeFetched(response.getData().getCharge_amount(), payload, response.getData().getFee());
                } catch (Exception e) {
                    mInteractor.showFetchFeeFailed(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mInteractor.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mInteractor.showFetchFeeFailed(message);
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

        mInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("USSD").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

                if (response.getData() != null) {
                    flwRef = response.getData().getUssdData().getFlw_reference();
                    publicKey = payload.getPBFPubKey();
                    String note = null;
                    if (response.getData().getNote() != null) note = response.getData().getNote();
                    else if (response.getData().getUssdData().getNote() != null)
                        note = response.getData().getUssdData().getNote();
                    else mInteractor.onPaymentError("No response data was returned");
                    if (note != null) {
                        if (note.contains("|")) {
                            ussdCode = note.substring(0, note.indexOf("|"));
                        } else ussdCode = note;
                        referenceCode = response.getData().getUssdData().getReference_code();
                        mInteractor.onUssdDetailsReceived(ussdCode, referenceCode);
                    }
                } else {
                    mInteractor.onPaymentError("No response data was returned");
                }

            }

            @Override
            public void onError(String message) {
                mInteractor.showProgressIndicator(false);
                mInteractor.onPaymentError(message);
            }
        });
    }

    @Override
    public void startPaymentVerification(int pollingTimeoutInSeconds) {
        requeryCountdownTime = System.currentTimeMillis();
        mInteractor.showPollingIndicator(true);
        requeryTx(flwRef, publicKey, requeryCountdownTime, pollingTimeoutInSeconds * 1000);
    }

    public void requeryTx(final String flwRef, final String publicKey, final long requeryCountdownTime, final long pollingTimeoutMillis) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {

            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {

                if (response.getData() == null) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getStatus().contains("fail")) {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    if (pollingCancelled) {
                        mInteractor.showPollingIndicator(false);
                        mInteractor.onPollingCanceled(flwRef, responseAsJSONString);
                    } else {
                        if ((System.currentTimeMillis() - requeryCountdownTime) < pollingTimeoutMillis) {
                            requeryTx(flwRef, publicKey, requeryCountdownTime, pollingTimeoutMillis);
                        } else {
                            mInteractor.showPollingIndicator(false);
                            mInteractor.onPollingTimeout(flwRef, responseAsJSONString);
                        }
                    }
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentSuccessful(flwRef, responseAsJSONString);
                } else {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mInteractor.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }
}