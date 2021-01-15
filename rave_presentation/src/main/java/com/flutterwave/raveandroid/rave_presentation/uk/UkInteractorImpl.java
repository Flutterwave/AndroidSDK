package com.flutterwave.raveandroid.rave_presentation.uk;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.FLUTTERWAVE_UK_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.FLUTTERWAVE_UK_BENEFICIARY_NAME;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.FLUTTERWAVE_UK_SORT_CODE;

class UkInteractorImpl implements UkContract.Interactor {

    private UkBankPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;
    private String txRef;


    UkInteractorImpl(UkBankPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullUkCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload, String fee) {
        feeCheckListener.onTransactionFeeFetched(chargeAmount, fee);
    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {
        try {
            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject responseJson = new Gson().fromJson(responseAsJSONString, type);
            flwRef = responseJson.getAsJsonObject("data").get("flwref").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onError("Transaction Failed", flwRef);
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {
        callback.onSuccessful(flwRef);
    }

    @Override
    public void showTransactionPage(String amount, String paymentCode, String accountNumber, String sortCode, final String flwRef, final String txRef) {
        callback.showTransactionDetails(
                amount,
                accountNumber != null ? accountNumber : FLUTTERWAVE_UK_ACCOUNT,
                sortCode != null ? sortCode : FLUTTERWAVE_UK_SORT_CODE,
                FLUTTERWAVE_UK_BENEFICIARY_NAME,
                paymentCode
        );
        this.flwRef = flwRef;
        this.txRef = txRef;
    }

    @Override
    public void showFetchFeeFailed(String errorMessage) {
        feeCheckListener.onFetchFeeError(errorMessage);
    }

    @Override
    public void onPaymentError(String errorMessage) {
        callback.onError(errorMessage, null);
    }

    @Override
    public void showPollingIndicator(boolean active) {
        // Todo: share validation message
        callback.showProgressIndicator(active);
    }

    public String getFlwRef() {
        return flwRef;
    }

    public String getTxRef() {
        return txRef;
    }

    public void setFeeCheckListener(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = (feeCheckListener != null) ? feeCheckListener : new NullFeeCheckListener();
    }
}
