

package com.flutterwave.raveandroid.rave_presentation.ussd;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;

public interface UssdContract {

    interface Interactor {
        void showProgressIndicator(boolean active);

        void showPollingIndicator(boolean active);

        void onPaymentError(String message);

        void onPaymentSuccessful(String flwRef, String responseAsString);

        void onTransactionFeeFetched(String charge_amount, Payload payload, String fee);

        void showFetchFeeFailed(String errorMessage);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onUssdDetailsReceived(String ussdCode, String referenceCode);

        void onPollingCanceled(String flwRef, String responseAsJSONString);

        void onPollingTimeout(String flwRef, String responseAsJSONString);
    }

    interface Handler {

        void fetchFee(Payload payload);

        void payWithUssd(Payload body, String encryptionKey);

        void startPaymentVerification(int pollingTimeoutInSeconds);

        void cancelPolling();

        void logEvent(Event event, String publicKey);
    }
}
