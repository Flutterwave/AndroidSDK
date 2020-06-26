package com.flutterwave.raveandroid.rave_presentation.ugmobilemoney;

import androidx.annotation.Nullable;

public class NullUgMobileMoneyPaymentCallback implements UgandaMobileMoneyPaymentCallback {

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
    public void showAuthenticationWebPage(String url) {

    }

}
