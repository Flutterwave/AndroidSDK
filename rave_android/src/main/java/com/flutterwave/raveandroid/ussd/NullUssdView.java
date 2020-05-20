package com.flutterwave.raveandroid.ussd;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

class NullUssdView implements UssdUiContract.View {
    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void showPollingIndicator(boolean active) {

    }

    @Override
    public void onPaymentError(String message) {

    }


    @Override
    public void onPaymentSuccessful(String status, String responseAsString) {

    }

    @Override
    public void onTransactionFeeFetched(String charge_amount, Payload payload, String fee) {

    }


    @Override
    public void showFetchFeeFailed(String s) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onPollingTimeout(String flwRef, String responseAsJSONString) {

    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void onAmountValidationFailed() {

    }

    @Override
    public void onUssdDetailsReceived(String ussdCode, String referenceCode) {

    }

    @Override
    public void onPollingCanceled(String flwRef, String responseAsJSONString) {

    }
}
