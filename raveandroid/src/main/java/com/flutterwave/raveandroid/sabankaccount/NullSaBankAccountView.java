package com.flutterwave.raveandroid.sabankaccount;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import java.util.HashMap;

public class NullSaBankAccountView implements SaBankAccountContract.View{

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showFetchFeeFailed(String s) {

    }

    @Override
    public void onPaymentError(String message) {

    }

    @Override
    public void showPollingIndicator(boolean active) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay, String currency) {

    }

    @Override
    public void displayFee(String charge_amount, Payload payload) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void onPollingRoundComplete(String flwRef, String txRef, String publicKey) {

    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsString) {

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef) {

    }

    @Override
    public void showWebView(String authUrl, String flwRef) {

    }
}
