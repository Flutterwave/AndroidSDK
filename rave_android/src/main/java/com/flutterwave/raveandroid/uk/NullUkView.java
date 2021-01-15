package com.flutterwave.raveandroid.uk;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

public class NullUkView implements UkUiContract.View {

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
    public void onTransactionFeeFetched(String charge_amount, Payload payload, String fee) {

    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {

    }


    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {

    }

    @Override
    public void showTransactionPage(String amount, String paymentCode, String accountNumber, String sortCode, String flwRef, String txRef) {

    }

}
