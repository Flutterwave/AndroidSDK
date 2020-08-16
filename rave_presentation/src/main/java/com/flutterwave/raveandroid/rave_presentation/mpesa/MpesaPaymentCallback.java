package com.flutterwave.raveandroid.rave_presentation.mpesa;

import androidx.annotation.Nullable;

public interface MpesaPaymentCallback {
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
}
