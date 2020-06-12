package com.flutterwave.raveandroid.rave_presentation.ghmobilemoney;

import androidx.annotation.Nullable;

public class NullGhMobileMoneyPaymentCallback implements GhanaMobileMoneyPaymentCallback {

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
