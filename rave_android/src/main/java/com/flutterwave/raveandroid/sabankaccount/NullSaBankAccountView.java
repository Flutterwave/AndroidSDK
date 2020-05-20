package com.flutterwave.raveandroid.sabankaccount;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

public class NullSaBankAccountView implements SaBankAccountUiContract.View {

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
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {

    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay, String currency) {

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
    public void onPaymentSuccessful(String status, String responseAsString) {

    }

    @Override
    public void showWebView(String authUrl, String flwRef) {

    }
}
