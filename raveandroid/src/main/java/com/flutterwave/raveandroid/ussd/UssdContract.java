

package com.flutterwave.raveandroid.ussd;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.Event;

import java.util.HashMap;

public interface UssdContract {

    interface View {
        void showProgressIndicator(boolean active);

        //
        void showPollingIndicator(boolean active);

        //
        void onPaymentError(String message);

        //
        void showToast(String message);

        //
        void onPaymentSuccessful(String status, String responseAsString);

        //
        void displayFee(String charge_amount, Payload payload);

        //
        void showFetchFeeFailed(String s);

        //
        void onPaymentFailed(String message, String responseAsJSONString);

        //
        void onPollingTimeout(String flwRef, String responseAsJSONString);

        //
        void onAmountValidationSuccessful(String amountToPay);

        //
        void showFieldError(int viewID, String message, Class<?> viewType);

        //
        void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        //
        void onAmountValidationFailed();

        void onUssdDetailsReceived(String ussdCode, String referenceCode);

        //
        void onPollingCanceled(String flwRef, final String responseAsJSONString);
    }

    interface UserActionsListener {

        void payWithUssd(Payload body, String encryptionKey);

        void init(RavePayInitializer ravePayInitializer);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void startPaymentVerification();

        void cancelPolling();

        void onAttachView(View view);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }
}
