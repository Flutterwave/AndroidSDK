package com.flutterwave.raveandroid.rave_presentation.card;

import androidx.annotation.Nullable;

public interface CardPaymentCallback {
    void showProgressIndicator(boolean active);

    void collectCardPin();

    void collectOtp(String message);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void collectAddress();

    void showAuthenticationWebPage(String authenticationUrl);
}
