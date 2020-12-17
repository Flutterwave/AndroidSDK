package com.flutterwave.raveandroid.rave_presentation.banktransfer;

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

/**
 * Created by hfetuga on 27/06/2018.
 */


public class BankTransferHandler implements BankTransferContract.BankTransferHandler {
    public boolean pollingCancelled = false;
    public boolean hasTransferDetails = false;
    protected String txRef = null, orderRef = null, flwRef = null, publicKey = null;
    protected String beneficiaryName;
    protected String accountNumber;
    protected String amount;
    protected String bankName;
    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    BankTransferContract.BankTransferInteractor mInteractor;
    private long requeryCountdownTime = 0;

    @Inject
    public BankTransferHandler(BankTransferContract.BankTransferInteractor mInteractor) {
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
                    e.printStackTrace();
                    mInteractor.onFetchFeeError("An error occurred while retrieving transaction fee");
                }
            }

            @Override
            public void onError(String message) {
                mInteractor.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mInteractor.onFetchFeeError(message);
            }
        });
    }

    @Override
    public void payWithBankTransfer(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = payloadToJson.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);
        encryptedCardRequestBody = encryptedCardRequestBody.trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("Bank Transfer").getEvent(), payload.getPBFPubKey());

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mInteractor.showProgressIndicator(false);

                if (response.getData() != null) {
                    hasTransferDetails = true;

                    flwRef = response.getData().getFlw_reference();
                    txRef = response.getData().getTx_ref();
                    orderRef = response.getData().getOrderRef();
                    publicKey = payload.getPBFPubKey();
                    if (response.getData().getNote() != null && response.getData().getNote().contains("to ")) {
                        beneficiaryName = response.getData().getNote().substring(
                                response.getData().getNote().indexOf("to ") + 3
                        );
                    }
                    amount = response.getData().getAmount();
                    accountNumber = response.getData().getAccountnumber();
                    bankName = response.getData().getBankname();
                    mInteractor.onTransferDetailsReceived(
                            amount,
                            accountNumber,
                            bankName,
                            beneficiaryName);
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
        requeryTx(flwRef, txRef, publicKey, pollingCancelled, requeryCountdownTime, pollingTimeoutInSeconds * 1000);
    }

    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }

    @Override
    public void requeryTx(final String flwRef, final String txRef, final String publicKey, final boolean pollingCancelled, final long requeryCountdownTime, final long pollingTimeoutMillis) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryPayWithBankTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mInteractor.showPollingIndicator(false);
                    mInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getStatus().contains("fail")) {
                    mInteractor.showProgressIndicator(false);
                    mInteractor.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("01")) {
                    if (pollingCancelled) {
                        mInteractor.showPollingIndicator(false);
                        mInteractor.onPollingCanceled(flwRef, txRef, responseAsJSONString);
                    } else {
                        if ((System.currentTimeMillis() - requeryCountdownTime) < pollingTimeoutMillis) {
                            requeryTx(flwRef, txRef, publicKey, pollingCancelled, requeryCountdownTime, pollingTimeoutMillis);
                        } else {
                            mInteractor.showPollingIndicator(false);
                            mInteractor.onPollingTimeout(flwRef, txRef, responseAsJSONString);
                        }
                    }
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
                mInteractor.showPollingIndicator(false);
                mInteractor.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }


}
