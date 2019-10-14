package com.flutterwave.raveandroid.ghmobilemoney;

import com.flutterwave.raveandroid.Payload;

/**
 * Created by hfetuga on 28/06/2018.
 */

public interface GhMobileMoneyContract {

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
    }

    interface UserActionsListener {
        void fetchFee(Payload payload);
        void chargeGhMobileMoney(Payload payload, String encryptionKey);
        void requeryTx(String flwRef, String txRef, String publicKey);
    }
}
