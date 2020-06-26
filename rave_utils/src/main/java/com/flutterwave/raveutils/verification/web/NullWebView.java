package com.flutterwave.raveutils.verification.web;

public class NullWebView implements WebContract.View {
    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onPaymentSuccessful(String responseAsString) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onPollingRoundComplete(String flwRef, String publicKey) {

    }
}
