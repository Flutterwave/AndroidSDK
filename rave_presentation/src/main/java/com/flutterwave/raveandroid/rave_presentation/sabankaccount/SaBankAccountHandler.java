package com.flutterwave.raveandroid.rave_presentation.sabankaccount;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaBankAccountResponse;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.inValidRedirectUrl;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

public class SaBankAccountHandler implements SaBankAccountContract.Handler {
    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    TransactionStatusChecker transactionStatusChecker;

    private SaBankAccountContract.Interactor mView;

    @Inject
    public SaBankAccountHandler(SaBankAccountContract.Interactor mView) {
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

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.onTransactionFeeRetrieved(response.getData().getCharge_amount(), payload, response.getData().getFee());
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
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }

    public void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString) {
        mView.showProgressIndicator(true);

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        mView.showProgressIndicator(false);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), responseAsJSONString);
        } else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void requeryTx(String publicKey, String flwRef) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setPBFPubKey(publicKey);
        body.setFlw_ref(flwRef);

        mView.showProgressIndicator(true);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(publicKey, body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                verifyRequeryResponseStatus(response, responseAsJSONString);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void chargeSaBankAccount(final Payload payload, String encryptionKey) {

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("SA Bank Account").getEvent(), payload.getPBFPubKey());


        networkRequest.chargeSaBankAccount(payload.getPBFPubKey(), payload, new ResultCallback<SaBankAccountResponse>() {
            @Override
            public void onSuccess(SaBankAccountResponse response) {

                mView.showProgressIndicator(false);
                String authUrl = response.getAuthUrl();
                if (authUrl != null) {
                    String flwRef = response.getData().getFlwRef();

                    mView.showWebView(authUrl, flwRef);
                } else {
                    mView.onPaymentError(inValidRedirectUrl);
                }

            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }
}
