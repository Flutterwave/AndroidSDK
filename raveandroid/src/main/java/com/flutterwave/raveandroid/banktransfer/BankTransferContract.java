package com.flutterwave.raveandroid.banktransfer;

import android.os.Bundle;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;


public interface BankTransferContract {

    interface View {
        void showProgressIndicator(boolean active);
        void showPollingIndicator(boolean active);
        void onPaymentError(String message);
        void showToast(String message);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
        void displayFee(String charge_amount, Payload payload);
        void showFetchFeeFailed(String s);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName);
        void onPollingTimeout(String flwRef, String txRef, String responseAsJSONString);
        void onAmountValidationSuccessful(String valueOf);
        void showFieldError(int viewID, String message, Class<?> viewType);
        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
        void onAmountValidationFailed();

        void onPollingCanceled(String flwRef, String txRef, final String responseAsJSONString);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void requeryTx();
        void payWithBankTransfer(Payload body, String encryptionKey);
        void init(RavePayInitializer ravePayInitializer);
        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);
        void onDataCollected(HashMap<String, ViewObject> dataHashMap);
        void startPaymentVerification();
        void cancelPolling();

        Bundle getState();

        void restoreState(Bundle savedInstanceState);
    }
}
