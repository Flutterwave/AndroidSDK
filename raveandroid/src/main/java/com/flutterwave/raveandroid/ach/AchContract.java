package com.flutterwave.raveandroid.ach;

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

        void onValidationSuccessful(String amount);

        void onAmountValidated(String amountToSet, int visibility);
    }

    interface UserActionsListener {
        void init(RavePayInitializer ravePayInitializer);

        void onPayButtonClicked(RavePayInitializer ravePayInitializer, String amount);

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void onFeeConfirmed(String authUrl, String flwRef);

        void processTransaction(String amount, RavePayInitializer ravePayInitializer, boolean isDisplayFee);

        void onAttachView(View view);

        void onDetachView();
    }

}
