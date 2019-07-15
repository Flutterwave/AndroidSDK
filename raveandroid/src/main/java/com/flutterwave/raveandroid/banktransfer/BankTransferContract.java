package com.flutterwave.raveandroid.banktransfer;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.responses.ChargeResponse;


public interface BankTransferContract {

    interface View {
        void showProgressIndicator(boolean active);
        void showPollingIndicator(boolean active);
        void onPollingRoundComplete(String flwRef, String txRef, String encryptionKey);
        void onPaymentError(String message);
        void showToast(String message);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
        void displayFee(String charge_amount, Payload payload);
        void showFetchFeeFailed(String s);
        void onPaymentFailed(String message, String responseAsJSONString);
        void onTransferDetailsReceived(ChargeResponse response);
        void onPollingTimeout(String flwRef, String txRef, String responseAsJSONString);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void requeryTx(String flwRef, String txRef, String publicKey);
        void payWithBankTransfer(Payload body, String encryptionKey);
        void setRequeryCountdownTime(long currentTimeMillis);
    }
}
