package com.flutterwave.raveandroid.rave_presentation.uk;

import android.support.annotation.Nullable;

public interface UkBankPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showTransactionDetails(String amount, String accountNumber, String sortCode, String beneficiaryName, String reference);

}
