package com.flutterwave.raveandroid.rave_presentation.ghmobilemoney;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.CHARGE_TYPE_GH_MOMO;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.REDIRECT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

/**
 * Created by hfetuga on 28/06/2018.
 */

public class GhMobileMoneyHandler implements GhMobileMoneyContract.Handler {
    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private GhMobileMoneyContract.Interactor mInteractor;
    private boolean pollingCancelled = false;
    private String txRef = null;
    private String flwRef = null;

    @Inject
    public GhMobileMoneyHandler(GhMobileMoneyContract.Interactor mInteractor) {
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
                    mInteractor.onTransactionFeeRetrieved(response.getData().getCharge_amount(), payload, response.getData().getFee());
                } catch (Exception e) {
                    e.printStackTrace();
                    mInteractor.showFetchFeeFailed(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mInteractor.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mInteractor.showFetchFeeFailed(message);
            }
        });
    }

    @Override
    public void chargeGhMobileMoney(final Payload payload, final String encryptionKey) {
        txRef = payload.getTx_ref();
        // Todo: merge code redundancies in different payment methods

        mInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("GH Mobile Money").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(payload.getPBFPubKey(), CHARGE_TYPE_GH_MOMO, payload, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

                if (response.getAuthMode() != null && response.getAuthMode().equalsIgnoreCase(REDIRECT) && response.getAuthUrl() != null)
                    mInteractor.showWebPage(response.getAuthUrl());
                else {
                    flwRef = response.getFlwRef();
                    requeryTx(flwRef, txRef, payload.getPBFPubKey());
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
    public void requeryTx(final String publicKey) {
        requeryTx(flwRef, txRef, publicKey);
    }

    @Override
    public void requeryTx(final String flwRef, final String txRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setTx_ref(txRef);
        body.setPBFPubKey(publicKey);

        mInteractor.showPollingIndicator(true);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(publicKey, body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getStatus() == null)
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                else if (response.getStatus().equalsIgnoreCase("pending")) {
                    if (!pollingCancelled) {
                        requeryTx(flwRef, txRef, publicKey);
                    } else mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getStatus().equalsIgnoreCase("successful")) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentSuccessful(flwRef, txRef, responseAsJSONString);
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
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }

    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }
}


