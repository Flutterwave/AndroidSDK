package com.flutterwave.raveandroid.rave_presentation.uk;

import androidx.annotation.Nullable;

public interface UkBankPaymentCallback {
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
     * Passes the details of the bank account to be transferred to. These details should be displayed
     * to the user for them to make the transfer and complete the transaction. {@link UkBankPaymentManager#checkTransactionStatus()}
     * should be called after this, or when the user has completed the transfer, to confirm the transaction status.
     *
     * @param amount          The amount to be transferred
     * @param accountNumber   The account number to be transferred to
     * @param sortCode        The sort code of the bank to be transferred to
     * @param beneficiaryName The name of the account to be transferred to
     * @param reference       A reference  to the transaction
     */
    void showTransactionDetails(String amount, String accountNumber, String sortCode, String beneficiaryName, String reference);

}
