package com.flutterwave.raveandroid.rave_presentation.ach;

import androidx.annotation.Nullable;

public class NullAchPaymentCallback implements AchPaymentCallback {

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
    public void showAuthenticationWebPage(String authenticationUrl) {

    }
}
