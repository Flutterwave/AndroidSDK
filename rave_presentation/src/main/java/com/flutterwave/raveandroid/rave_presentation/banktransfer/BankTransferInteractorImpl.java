package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class BankTransferInteractorImpl implements BankTransferContract.BankTransferInteractor {

    private BankTransferPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;


    BankTransferInteractorImpl(BankTransferPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullBankTransferPaymentCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString) {
        this.flwRef = flwRef;
        callback.onSuccessful(flwRef);
    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload, String fee) {
        feeCheckListener.onTransactionFeeFetched(chargeAmount, fee);
    }

    @Override
    public void onFetchFeeError(String errorMessage) {
        feeCheckListener.onFetchFeeError(errorMessage);
    }


    @Override
    public void onPaymentFailed(String message, String responseAsJsonString) {
        try {
            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject responseJson = new Gson().fromJson(responseAsJsonString, type);
            flwRef = responseJson.getAsJsonObject("data").get("flwref").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onError(message, flwRef);
    }

    @Override
    public void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName) {
        callback.onTransferDetailsReceived(amount, accountNumber, bankName, beneficiaryName);
    }

    @Override
    public void onPollingTimeout(String flwRef, String txRef, String responseAsJSONString) {
        callback.onPollingTimeout(flwRef);
    }

    @Override
    public void onPollingCanceled(String flwRef, String txRef, String responseAsJSONString) {
        // User to handle this
    }

    @Override
    public void onPaymentError(String errorMessage) {
        callback.onError(errorMessage, null);
    }

    @Override
    public void showPollingIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    public String getFlwRef() {
        return flwRef;
    }

    public void setFeeCheckListener(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = (feeCheckListener != null) ? feeCheckListener : new NullFeeCheckListener();
    }
}
