package com.flutterwave.raveandroid.rave_presentation.ghmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_remote.responses.MobileMoneyChargeResponse;


public interface GhMobileMoneyContract {

    interface Interactor {
        void showFetchFeeFailed(String s);
        void onPaymentError(String message);
        void showPollingIndicator(boolean active, String validateInstruction);
        void showProgressIndicator(boolean active);

        void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void showWebPage(String captchaLink);
    }

    interface Handler {
        void fetchFee(Payload payload);
        void requeryTx(String flwRef, String txRef, String publicKey, MobileMoneyChargeResponse.Data data);
        void chargeGhMobileMoney(Payload payload, String encryptionKey);
        void logEvent(Event event, String publicKey);

        void cancelPolling();

        void requeryTx(String publicKey);
    }
}
