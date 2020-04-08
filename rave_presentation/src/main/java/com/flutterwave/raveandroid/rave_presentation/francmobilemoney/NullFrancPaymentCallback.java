package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import android.support.annotation.Nullable;

public class NullFrancPaymentCallback implements FrancophonePaymentCallback {

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
