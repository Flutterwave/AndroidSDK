package com.flutterwave.raveandroid.rave_presentation.acquireddotcom;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;

public interface AcquiredContract {
    interface Interactor {
        void showFetchFeeFailed(String s);
        void onPaymentError(String message);
        void showPollingIndicator(boolean active);
        void showProgressIndicator(boolean active);

        void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onPaymentSuccessful(String status, String responseAsString);
        void showWebView(String authUrl, String flwRef);
    }

    interface Handler {
        void fetchFee(Payload payload);
        void requeryTx(String publicKey, String flwRef);
        void chargeAcquired(Payload payload, String encryptionKey);
        void logEvent(Event event, String publicKey);
    }
}
