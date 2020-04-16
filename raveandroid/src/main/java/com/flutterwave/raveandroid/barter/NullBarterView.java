package com.flutterwave.raveandroid.barter;


import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

public class NullBarterView implements BarterUiContract.View {
    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void showFetchFeeFailed(String message) {

    }

    @Override
    public void loadBarterCheckout(String authUrlCrude, String flwRef) {

    }

    @Override
    public void onPaymentError(String message) {

    }

    @Override
    public void onTransactionFeeFetched(String charge_amount, Payload payload, String fee) {

    }

    @Override
    public void showPollingIndicator(boolean active) {

    }

    @Override
    public void onPaymentSuccessful(String flwRef, String responseAsString) {

    }

    @Override
    public void onPaymentFailed(String flwRef, String responseAsJSONString) {

    }

    @Override
    public void onPollingCanceled(String flwRef, String responseAsJSONString) {

    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {

    }
}
