package com.flutterwave.raveandroid.rave_presentation.uk;

import androidx.annotation.Nullable;

public class NullUkCallback implements UkBankPaymentCallback {

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {

    }

    @Override
    public void onSuccessful(String flwRef) {

    }

    @Override
    public void showTransactionDetails(String amount, String accountNumber, String sortCode, String beneficiaryName, String reference) {

    }

}
