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
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.CHARGE_TYPE_USSD;
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

        mInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("USSD").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(payload.getPBFPubKey(), CHARGE_TYPE_USSD, payload, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

                String code = response.getUssdCode();

                if (code != null) {
                    flwRef = response.getFlwRef();
                    publicKey = payload.getPBFPubKey();
                    if (code.contains("|")) {
                        ussdCode = code.substring(0, code.indexOf("|"));
                        if (code.length() > code.indexOf("|") + 1)
                            referenceCode = code.substring(code.indexOf("|") + 1);
                    } else ussdCode = code;
                    mInteractor.onUssdDetailsReceived(ussdCode, referenceCode);
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

        networkRequest.requeryTx(publicKey, body, new Callbacks.OnRequeryRequestComplete() {

            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getStatus() == null)
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                else if (response.getStatus().equalsIgnoreCase("pending")) {
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

                } else if (response.getStatus().equalsIgnoreCase("successful")) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentSuccessful(flwRef, responseAsJSONString);
                } else {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
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