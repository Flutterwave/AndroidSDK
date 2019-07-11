package com.flutterwave.raveandroid.mpesa;

import android.app.Activity;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface MpesaContract {

    interface View {
        void showProgressIndicator(boolean active);
        void showPollingIndicator(boolean active);
        void onPollingRoundComplete(String flwRef, String txRef, String encryptionKey);
        void onPaymentError(String message);
        void showToast(String message);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
        void displayFee(String charge_amount, Payload payload);
        void showFetchFeeFailed(String s);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
        void showFieldError(int viewID, String message, Class<?> viewType);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void chargeMpesa(Payload payload, String encryptionKey);
        void requeryTx(String flwRef, String txRef, String publicKey);
        void validate(HashMap<String, ViewObject> dataHashMap);
        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer, Activity activity);
    }
}
