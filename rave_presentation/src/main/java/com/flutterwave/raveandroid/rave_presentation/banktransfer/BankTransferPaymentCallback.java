package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import androidx.annotation.Nullable;

public interface BankTransferPaymentCallback {
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
     * to the user for them to make the transfer and complete the transaction. {@link BankTransferPaymentManager#checkTransactionStatus(int)}
     * should be called after this, or when the user has completed the transfer, to confirm the transaction status.
     *
     * @param amount          The amount to be transferred
     * @param accountNumber   The account number to be transferred to
     * @param bankName        The name of the bank to be transferred to
     * @param beneficiaryName The name of the account to be transferred to
     */
    void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName);

    /**
     * Called when the bank transfer has not been confirmed in the timeout when calling {@link BankTransferPaymentManager#checkTransactionStatus(int)}.
     *
     * @param flwRef
     */
    void onPollingTimeout(String flwRef);
}
