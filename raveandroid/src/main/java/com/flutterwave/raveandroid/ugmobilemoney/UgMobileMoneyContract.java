package com.flutterwave.raveandroid.ugmobilemoney;

import android.app.Activity;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

/**
 * Created by Jeremiah on 28/06/2018.
 */

public interface UgMobileMoneyContract {

    interface View {
        void showProgressIndicator(boolean active);
        void showPollingIndicator(boolean active);
        void onPollingRoundComplete(String flwRef, String txRef, String publicKey);
        void onPaymentError(String message);
        void showToast(String message);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
        void displayFee(String charge_amount, Payload payload);
        void showFetchFeeFailed(String s);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
        void showFieldError(int viewID, String message);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void chargeUgMobileMoney(Payload payload, String encryptionKey);
        void requeryTx(String flwRef, String txRef, String publicKey);
        void validate(HashMap<String, ViewObject> dataHashMap);
        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer, Activity activity);
    }
}
