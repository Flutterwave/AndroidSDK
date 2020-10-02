package com.flutterwave.raveandroid.acquireddotcom;

import com.flutterwave.raveandroid.rave_java_commons.Payload;

public class NullAcquiredView implements AcquiredUiContract.View {
    @Override
    public void onAmountValidationSuccessful(String amountToPay, String currency) {

    }

    @Override
    public void onAmountValidationFailed() {

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
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsString) {

    }

    @Override
    public void showWebView(String authUrl, String flwRef) {

    }
}
