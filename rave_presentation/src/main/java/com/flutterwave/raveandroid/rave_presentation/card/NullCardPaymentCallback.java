package com.flutterwave.raveandroid.rave_presentation.card;

import androidx.annotation.Nullable;

public class NullCardPaymentCallback implements CardPaymentCallback {
    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void collectCardPin() {

    }

    @Override
    public void collectOtp(String message) {

    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {

    }

    @Override
    public void onSuccessful(String flwRef) {

    }

    @Override
    public void collectAddress() {

    }

    @Override
    public void showAuthenticationWebPage(String authenticationUrl) {

    }
}
