package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;


public interface BankTransferContract {

    interface BankTransferInteractor {
        void onTransactionFeeFetched(String charge_amount, Payload payload, String fee);

        void onFetchFeeError(String s);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName);

        void onPollingTimeout(String flwRef, String txRef, String responseAsJSONString);

        void onPollingCanceled(String flwRef, String txRef, final String responseAsJSONString);

        void showProgressIndicator(boolean active);

        void onPaymentError(String errorMessage);

        void showPollingIndicator(boolean active);

        void onPaymentSuccessful(String flwRef, String txRef, String responseAsJSONString);
    }

    interface BankTransferHandler {
        void fetchFee(Payload payload);

        void requeryTx(String flwRef, String txRef, String publicKey, boolean pollingCancelled, long requeryCountdownTime, long pollingTimeoutMillis);

        void payWithBankTransfer(Payload body, String encryptionKey);

        void startPaymentVerification(int pollingTimeoutInSeconds);

        void cancelPolling();

        void logEvent(Event event, String publicKey);
    }
}
