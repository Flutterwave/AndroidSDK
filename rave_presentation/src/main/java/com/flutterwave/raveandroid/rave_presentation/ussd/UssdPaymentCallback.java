package com.flutterwave.raveandroid.rave_presentation.ussd;

import androidx.annotation.Nullable;

public interface UssdPaymentCallback {
    /**
     * Called to indicate that a background task is running, e.g. a network call.
     * This should typically show or hide a progress bar.
     *
     * @param active If true, background task is running. If false, background task has stopped
     */
    void showProgressIndicator(boolean active);

    /**
     * Called when an error occurs with the payment. The error message can be displayed to the users.
     *
     * @param errorMessage A message describing the error
     * @param flwRef       The Flutterwave reference to the transaction.
     */
    void onError(String errorMessage, @Nullable String flwRef);

    /**
     * Called when the transaction has been completed successfully.
     *
     * @param flwRef The Flutterwave reference to the transaction.
     */
    void onSuccessful(String flwRef);

    /**
     * Called when the bank transfer has not been confirmed in the timeout when calling {@link UssdPaymentManager#checkTransactionStatus(int)}.
     *
     * @param flwRef
     */
    void onPollingTimeout(String flwRef);

    /**
     * Passes the details of the USSD code to be used to complete this transaction. These details should be displayed
     * to the user for them to complete the transaction. {@link UssdPaymentManager#checkTransactionStatus(int)}
     * should be called after this, or when the user has completed the USSD flow, to confirm the transaction status.
     *
     * @param ussdCode      The USSD code to be dailed.
     * @param referenceCode A reference code for the transaction (to also be shown to the user).
     */
    void onUssdDetailsReceived(String ussdCode, String referenceCode);
}
