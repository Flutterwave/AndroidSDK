package com.flutterwave.raveandroid.rave_presentation.ach;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
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
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;


public class AchHandler implements AchContract.Handler {

    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private AchContract.Interactor interactor;

    @Inject
    public AchHandler(AchContract.Interactor interactor) {
        this.interactor = interactor;
    }

    public void chargeAccount(Payload payload, String encryptionKey) {

        String requestBodyAsString = payloadToJsonConverter.convertChargeRequestPayloadToJson(payload);
        String accountRequestBody = payloadEncryptor.getEncryptedData(requestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(accountRequestBody);

        interactor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("ACH").getEvent(), payload.getPBFPubKey());

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                interactor.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getAuthurl() != null) {
                        String authUrl = response.getData().getAuthurl();
                        String flwRef = response.getData().getFlwRef();
                        String chargedAmount = response.getData().getChargedAmount();
                        String currency = response.getData().getCurrency();


                        interactor.showWebView(authUrl, flwRef);

                    } else {
                        interactor.onPaymentError(RaveConstants.no_authurl_was_returnedmsg);
                    }

                } else {
                    interactor.onPaymentError(RaveConstants.noResponse);
                }

            }

            @Override
            public void onError(String message) {
                interactor.showProgressIndicator(false);
                interactor.onPaymentError(message);
            }
        });

    }

    public void requeryTx(String flwRef, String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        interactor.showProgressIndicator(true);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                interactor.showProgressIndicator(false);
                verifyRequeryResponseStatus(responseAsJSONString);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                interactor.onPaymentFailed(responseAsJSONString);
            }
        });
    }

    public void verifyRequeryResponseStatus(String responseAsJSONString) {
        interactor.showProgressIndicator(true);

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        interactor.showProgressIndicator(false);

        if (wasTxSuccessful) {
            interactor.onPaymentSuccessful(responseAsJSONString);
        } else {
            interactor.onPaymentFailed(responseAsJSONString);
        }
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }

    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPBFPubKey(payload.getPBFPubKey());

        interactor.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                interactor.showProgressIndicator(false);

                try {
                    interactor.onTransactionFeeRetrieved(response.getData().getCharge_amount(), payload, response.getData().getFee());
                } catch (Exception e) {
                    e.printStackTrace();
                    interactor.onFeeFetchError(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                interactor.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                interactor.onFeeFetchError(message);
            }
        });

    }
}
