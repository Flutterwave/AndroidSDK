package com.flutterwave.raveandroid.barter;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

public interface BarterContract {

    interface View {


        void showFieldError(int viewID, String message, Class<?> viewType);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showProgressIndicator(boolean active);

        void showFetchFeeFailed(String message);

        void loadBarterCheckout(String authUrlCrude, String flwRef);

        void onPaymentError(String message);

        void showToast(String message);

        void displayFee(String charge_amount, Payload payload);

        void showPollingIndicator(boolean active);

        void onPaymentSuccessful(String responseAsString);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPollingRoundComplete(String flwRef, String publicKey);

        void onAmountValidationSuccessful(String amountToPay);

    }

    interface UserActionsListener {

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void chargeBarter(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String publicKey);

        void fetchFee(Payload payload);

        void onAttachView(View view);

        void onDetachView();
    }
}
