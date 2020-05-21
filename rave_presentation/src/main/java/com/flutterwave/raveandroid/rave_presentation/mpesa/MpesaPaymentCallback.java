package com.flutterwave.raveandroid.rave_presentation.mpesa;

import androidx.annotation.Nullable;

public interface MpesaPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
