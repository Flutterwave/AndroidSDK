package com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney;

import androidx.annotation.Nullable;

public class NullRwfMobileMoneyPaymentCallback implements RwfMobileMoneyPaymentCallback {

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
