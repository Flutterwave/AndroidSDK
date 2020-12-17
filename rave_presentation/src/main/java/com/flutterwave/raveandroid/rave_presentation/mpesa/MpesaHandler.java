package com.flutterwave.raveandroid.rave_presentation.mpesa;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.Utils;
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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

/**
 * Created by hfetuga on 27/06/2018.
 */


public class MpesaHandler implements MpesaContract.Handler {

    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private MpesaContract.Interactor mInteractor;
    private boolean pollingCancelled = false;

    @Inject
    public MpesaHandler(MpesaContract.Interactor mInteractor) {
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
    public void chargeMpesa(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey)
                .trim().replaceAll("\\n", "");

//        Log.d("encrypted", encryptedCardRequestBody);

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("MPesa").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

                if (response.getData() != null) {
                    String flwRef = response.getData().getFlwRef();
                    String txRef = response.getData().getTx_ref();
                    requeryTx(flwRef, txRef, payload.getPBFPubKey());
                } else {
                    mInteractor.onPaymentError(noResponse);
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
    public void requeryTx(final String flwRef, final String txRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mInteractor.showPollingIndicator(true);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getStatus().contains("fail")) {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    if (!pollingCancelled) {
                        requeryTx(flwRef, txRef, publicKey);
                    } else mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentSuccessful(flwRef, txRef, responseAsJSONString);
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
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }


    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }
}
