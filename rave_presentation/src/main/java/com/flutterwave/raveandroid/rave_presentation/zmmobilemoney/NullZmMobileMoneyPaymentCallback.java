package com.flutterwave.raveandroid.rave_presentation.zmmobilemoney;

import androidx.annotation.Nullable;

public class NullZmMobileMoneyPaymentCallback implements ZambiaMobileMoneyPaymentCallback {

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
