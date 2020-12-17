package com.flutterwave.raveandroid.rave_presentation.barter;


import android.net.Uri;
import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.Utils;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import java.util.Set;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

public class BarterHandler implements BarterContract.Handler {

    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private BarterContract.Interactor mInteractor;
    private boolean pollingCancelled = false;
    private boolean automaticRequery = false;


    @Inject
    public BarterHandler(BarterContract.Interactor mInteractor) {
        this.mInteractor = mInteractor;
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

        mInteractor.showProgressIndicator(true);

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

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

                        mInteractor.loadBarterCheckout(authUrlCrude, flwRef);
                        if (automaticRequery) requeryTx(flwRef, payload.getPBFPubKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                        mInteractor.onPaymentError("An error occurred with your payment. Please try again or contact support.");
                    }
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
    public void requeryTx(final String flwRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setOrder_ref(flwRef); // Uses Order ref instead of flwref
        body.setPBFPubKey(publicKey);

        mInteractor.showPollingIndicator(true);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mInteractor.onPaymentFailed(flwRef, responseAsJSONString);
                } else if (response.getData().getStatus().contains("fail")) {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("02")) {
                    if (pollingCancelled) {
                        mInteractor.showPollingIndicator(false);
                        mInteractor.onPollingCanceled(flwRef, responseAsJSONString);
                    } else requeryTx(flwRef, publicKey);
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentSuccessful(flwRef, responseAsJSONString);
                } else {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(flwRef, responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mInteractor.onPaymentFailed(flwRef, responseAsJSONString);
            }
        });
    }


    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
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
                Log.e(RAVEPAY, message);
                mInteractor.showFetchFeeFailed(message);
            }
        });
    }


    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }

    public void setAutomaticRequery(boolean automaticRequery) {
        this.automaticRequery = automaticRequery;
    }
}
