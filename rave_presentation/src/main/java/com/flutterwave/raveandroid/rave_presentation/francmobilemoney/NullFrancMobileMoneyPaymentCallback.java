package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import androidx.annotation.Nullable;

public class NullFrancMobileMoneyPaymentCallback implements FrancophoneMobileMoneyPaymentCallback {

    @Override
    public void showProgressIndicator(boolean active, String note) {

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
