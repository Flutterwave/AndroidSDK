package com.flutterwave.raveutils.verification.web;

public interface WebContract {

    interface View {


        void showProgressIndicator(boolean active);

        void onPaymentSuccessful(String responseAsString);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPollingRoundComplete(String flwRef, String publicKey);

    }

    interface UserActionsListener {


        void init(String flwRef, String publicKey, Boolean isBarter);

        void requeryTx(String flwRef, String publicKey, Boolean isBarter);

        void onAttachView(View view);

        void onDetachView();
    }
}
