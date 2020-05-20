package com.flutterwave.raveandroid.rave_presentation.sabankaccount;

import androidx.annotation.Nullable;

public interface SaBankAccountPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showAuthenticationWebPage(String authUrl);
}
