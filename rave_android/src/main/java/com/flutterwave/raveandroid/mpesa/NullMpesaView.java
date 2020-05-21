package com.flutterwave.raveandroid.mpesa;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

public class NullMpesaView implements MpesaUiContract.View {

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
    public void onPhoneValidated(String phoneToSet, boolean isEditable) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {

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
}
