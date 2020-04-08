package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface FrancMobileMoneyContract {

    interface Interactor {
        void showFetchFeeFailed(String s);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active);

        void showProgressIndicator(boolean active);

        void onTransactionFeeRetrieved(String charge_amount, Payload payload, String fee);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
    }

    interface Handler {
        void fetchFee(Payload payload);

        void chargeFranc(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String txRef, String publicKey);

        void logEvent(Event event, String publicKey);
    }
}
