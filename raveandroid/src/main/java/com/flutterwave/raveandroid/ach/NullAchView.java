package com.flutterwave.raveandroid.ach;

import com.flutterwave.raveandroid.rave_java_commons.Payload;

public class NullAchView implements AchUiContract.View {
    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onAmountValidated(String amountToSet, int visibility) {

    }

    @Override
    public void showRedirectMessage(boolean b) {

    }

    @Override
    public void onPaymentError(String message) {

    }

    @Override
    public void showWebView(String authUrl, String flwRef) {

    }

    @Override
    public void onPaymentFailed(String responseAsJSONString) {

    }

    @Override
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {

    }

    @Override
    public void onPaymentSuccessful(String responseAsJSONString) {

    }

    @Override
    public void onFeeFetchError(String errorMessage) {

    }

    @Override
    public void showAmountError(String msg) {

    }

    @Override
    public void showFee(String authUrl, String flwRef, String chargedAmount, String currency) {

    }

    @Override
    public void onValidationSuccessful(String amount) {

    }
}
