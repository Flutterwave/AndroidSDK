package com.flutterwave.raveandroid.rave_presentation.zmmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;

/**
 * Created by hfetuga on 28/06/2018.
 */

public interface ZmMobileMoneyContract {

    interface Interactor {
        void showFetchFeeFailed(String s);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active);

        void showProgressIndicator(boolean active);

        void onTransactionFeeFetched(String charge_amount, Payload payload, String fee);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void showWebPage(String link);
    }

    interface Handler {
        void fetchFee(Payload payload);

        void requeryTx(String publicKey);

        void requeryTx(String flwRef, String txRef, String publicKey);

        void chargeZmMobileMoney(Payload payload, String encryptionKey);

        void logEvent(Event event, String publicKey);

        void cancelPolling();
    }
}
