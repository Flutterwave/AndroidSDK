package com.flutterwave.raveandroid.mpesa;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.Event;

import java.util.HashMap;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface MpesaContract {

    interface View {
        void showToast(String message);

        void showFetchFeeFailed(String s);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active);

        void showProgressIndicator(boolean active);

        void onAmountValidationSuccessful(String amountToPay);

        void displayFee(String charge_amount, Payload payload);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onPollingRoundComplete(String flwRef, String txRef, String encryptionKey);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void chargeMpesa(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String txRef, String publicKey);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }
}
