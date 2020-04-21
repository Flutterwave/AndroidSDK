package com.flutterwave.raveandroid.rave_presentation.barter;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class BarterInteractorImpl implements BarterContract.Interactor {

    private BarterPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;


    BarterInteractorImpl(BarterPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullBarterPaymentCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void onPaymentSuccessful(String flwRef, String responseAsJSONString) {
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
    public void loadBarterCheckout(String authUrlCrude, String flwRef) {
        callback.loadBarterCheckout(authUrlCrude, flwRef);
    }


    @Override
    public void onPaymentFailed(String flwRef, String responseAsJsonString) {
        try {
            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject responseJson = new Gson().fromJson(responseAsJsonString, type);
            flwRef = responseJson.getAsJsonObject("data").get("flwref").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onError("Payment Failed", flwRef);
    }

    @Override
    public void onPollingCanceled(String flwRef, String responseAsJSONString) {
        // User to handle
    }

    @Override
    public void onPaymentError(String errorMessage) {
        callback.onError(errorMessage, flwRef);
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
