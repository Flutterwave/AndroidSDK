package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import android.support.annotation.Nullable;

public interface BankTransferCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName);

    void onPollingTimeout(String flwRef);
}
