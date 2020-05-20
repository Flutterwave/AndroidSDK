package com.flutterwave.raveandroid.banktransfer;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

public class NullBankTransferView implements BankTransferUiContract.View {

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
    public void showToast(String message) {

    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {

    }

    @Override
    public void onTransactionFeeFetched(String charge_amount, Payload payload, String fee) {

    }

    @Override
    public void onFetchFeeError(String s) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName) {

    }

    @Override
    public void onPollingTimeout(String flwRef, String txRef, String responseAsJSONString) {

    }

    @Override
    public void onAmountValidationSuccessful(String valueOf) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void onAmountValidationFailed() {

    }

    @Override
    public void onPollingCanceled(String flwRef, String txRef, String responseAsJSONString) {

    }
}
