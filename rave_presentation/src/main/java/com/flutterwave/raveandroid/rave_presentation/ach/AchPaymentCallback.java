package com.flutterwave.raveandroid.rave_presentation.ach;

import androidx.annotation.Nullable;

public interface AchPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showAuthenticationWebPage(String authenticationUrl);
}
