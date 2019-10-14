package com.flutterwave.raveandroid.ach;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.responses.RequeryResponse;

public interface AchContract {

    interface View {
        void showProgressIndicator(boolean active);

        void showAmountField(boolean active);

        void showRedirectMessage(boolean b);

        void onPaymentError(String message);

        void showWebView(String authUrl, String flwRef);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString);

        void showAmountError(String msg);

        void showFee(String authUrl, String flwRef, String chargedAmount, String currency);
    }

    interface UserActionsListener {
        void onStartAchPayment(RavePayInitializer ravePayInitializer);

        void onPayButtonClicked(RavePayInitializer ravePayInitializer, String amount);

        void chargeAccount(Payload payload, String encryptionKey, boolean isDisplayFee);

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void onFeeConfirmed(String authUrl, String flwRef);
    }

}
