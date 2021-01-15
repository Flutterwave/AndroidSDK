package com.flutterwave.raveandroid.rave_presentation.uk;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;


public interface UkContract {

    interface Interactor {
        void showFetchFeeFailed(String s);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active);

        void showProgressIndicator(boolean active);

        void onTransactionFeeFetched(String charge_amount, Payload payload, String fee);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void showTransactionPage(String amount, String paymentCode, String accountNumber, String sortCode, String flwRef, String txRef);
    }

    interface Handler {
        void fetchFee(Payload payload);

        void chargeUk(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String txRef, String publicKey);

        void logEvent(Event event, String publicKey);

        void cancelPolling();
    }
}
