package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.NullFeeCheckListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class FrancMobileMoneyInteractorImpl implements FrancMobileMoneyContract.Interactor {

    private FrancophoneMobileMoneyPaymentCallback callback;
    private String flwRef;
    private FeeCheckListener feeCheckListener;


    FrancMobileMoneyInteractorImpl(FrancophoneMobileMoneyPaymentCallback callback) {
        this.callback = (callback != null) ? callback : new NullFrancMobileMoneyPaymentCallback();
        this.feeCheckListener = new NullFeeCheckListener();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active, "");
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
    public void onPaymentSuccessful(String flwRef, String responseAsString) {
        callback.onSuccessful(flwRef);
    }

    @Override
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {
        feeCheckListener.onTransactionFeeFetched(chargeAmount, fee);
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
    public void showPollingIndicator(boolean active, String note) {
        callback.showProgressIndicator(active,note);
    }

    @Override
    public void showWebPage(String authenticationUrl, String flwRef) {
        this.flwRef = flwRef;
        callback.showAuthenticationWebPage(authenticationUrl);
    }
    public String getFlwRef() {
        return flwRef;
    }

    public void setFeeCheckListener(FeeCheckListener feeCheckListener) {
        this.feeCheckListener = (feeCheckListener != null) ? feeCheckListener : new NullFeeCheckListener();
    }
}
