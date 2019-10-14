package com.flutterwave.raveandroid.uk;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.responses.ChargeResponse;

/**
 * Created by Jeremiah on 28/06/2018.
 */

public interface UkBankAccountContract {

    interface View {
        void showProgressIndicator(boolean active);
        void showPollingIndicator(boolean active);
        void onPollingRoundComplete(String flwRef, String txRef, String publicKey);
        void onPaymentError(String message);
        void showToast(String message);
        void onPaymentSuccessful(String status, String flwRef, String responseAsString);
        void displayFee(String charge_amount, Payload payload);
        void showFetchFeeFailed(String s);
        void onPaymentFailed(String message, String responseAsJSONString);
        void showTransactionPage(ChargeResponse response);
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void chargeUkBankAccount(Payload payload, String encryptionKey);
        void requeryTx(String flwRef, String txRef, String publicKey);
    }
}
