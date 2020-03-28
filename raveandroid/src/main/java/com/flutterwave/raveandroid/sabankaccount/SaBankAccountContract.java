package com.flutterwave.raveandroid.sabankaccount;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import java.util.HashMap;

public interface SaBankAccountContract {
    interface View {
        void showToast(String message);
        void showFetchFeeFailed(String s);
        void onPaymentError(String message);
        void showPollingIndicator(boolean active);
        void showProgressIndicator(boolean active);
        void onAmountValidationSuccessful(String amountToPay, String currency);
        void displayFee(String charge_amount, Payload payload);
        void showFieldError(int viewID, String message, Class<?> viewType);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
        void onPollingRoundComplete(String flwRef, String txRef, String publicKey);
        void onPaymentSuccessful(String status, String responseAsString);
        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);
        void showWebView(String authUrl, String flwRef);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void init(RavePayInitializer ravePayInitializer);
        void onDataCollected(HashMap<String, ViewObject> dataHashMap);
        void requeryTx(String publicKey);
        void chargeSaBankAccount(Payload payload, String encryptionKey);
        void processTransaction(RavePayInitializer ravePayInitializer);

        void onAttachView(SaBankAccountContract.View view);

        void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }
}
