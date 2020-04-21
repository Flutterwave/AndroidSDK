package com.flutterwave.raveandroid.rave_presentation.mpesa;

import android.support.annotation.Nullable;

public class NullMpesaPaymentCallback implements MpesaPaymentCallback {

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {

    }

    @Override
    public void onSuccessful(String flwRef) {

    }

}
