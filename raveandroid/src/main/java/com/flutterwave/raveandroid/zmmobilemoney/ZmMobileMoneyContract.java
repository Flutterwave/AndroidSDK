package com.flutterwave.raveandroid.zmmobilemoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;

/**
 * Created by hfetuga on 28/06/2018.
 */

public interface ZmMobileMoneyContract {

    interface View {
        void showToast(String message);

        void showFetchFeeFailed(String s);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active);

        void showProgressIndicator(boolean active);

        void onAmountValidationSuccessful(String valueOf);

        void displayFee(String charge_amount, Payload payload);

        void onPaymentFailed(String message, String responseAsJSONString);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onPollingRoundComplete(String flwRef, String txRef, String publicKey);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

    }

    interface UserActionsListener {
        void fetchFee(Payload payload);

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void requeryTx(String flwRef, String txRef, String publicKey);

        void chargeZmMobileMoney(Payload payload, String encryptionKey);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }
}
