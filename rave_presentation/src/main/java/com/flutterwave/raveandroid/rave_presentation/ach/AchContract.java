package com.flutterwave.raveandroid.rave_presentation.ach;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;

public interface AchContract {

    interface Interactor {
        void showProgressIndicator(boolean active);

        void onPaymentError(String message);

        void showWebView(String authUrl, String flwRef);

        void onPaymentFailed(String responseAsJSONString);

        void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee);

        void onPaymentSuccessful(String responseAsJSONString);

        void onFeeFetchError(String errorMessage);
    }

    interface Handler {
        void logEvent(Event event, String publicKey);
    }

}
