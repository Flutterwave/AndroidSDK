package com.flutterwave.raveandroid.rave_presentation.ussd;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class UssdInteractorImpl implements UssdContract.Interactor {

    private UssdPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;


    UssdInteractorImpl(UssdPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullUssdCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void onPaymentSuccessful(String flwRef, String responseAsString) {
        this.flwRef = flwRef;
        callback.onSuccessful(flwRef);
    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload, String fee) {
        feeCheckListener.onTransactionFeeFetched(chargeAmount, fee);
    }


    @Override
    public void showFetchFeeFailed(String errorMessage) {
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
    public void onUssdDetailsReceived(String ussdCode, String referenceCode) {
        callback.onUssdDetailsReceived(ussdCode, referenceCode);
    }

    @Override
    public void onPollingCanceled(String flwRef, String responseAsJSONString) {
        // User to handle this

    }

    @Override
    public void onPollingTimeout(String flwRef, String responseAsJSONString) {
        callback.onPollingTimeout(flwRef);

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
