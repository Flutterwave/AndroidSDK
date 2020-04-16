package com.flutterwave.raveandroid.rave_presentation.barter;


import com.flutterwave.raveandroid.rave_java_commons.Payload;

public interface BarterContract {

    interface Interactor {
        void showProgressIndicator(boolean active);

        void showFetchFeeFailed(String message);

        void loadBarterCheckout(String authUrlCrude, String flwRef);

        void onPaymentError(String message);

        void onTransactionFeeFetched(String charge_amount, Payload payload, String fee);

        void showPollingIndicator(boolean active);

        void onPaymentSuccessful(String flwRef, String responseAsString);

        void onPaymentFailed(String flwRef, String responseAsJSONString);

        void onPollingCanceled(String flwRef, String responseAsJSONString);
    }

    interface Handler {

        void chargeBarter(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String publicKey);

        void fetchFee(Payload payload);

        void cancelPolling();
    }
}
