package com.flutterwave.raveandroid.rave_presentation.barter;

import androidx.annotation.Nullable;

public class NullBarterPaymentCallback implements BarterPaymentCallback {
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
    public void loadBarterCheckout(String authenticationUrl, String flwRef) {

    }
}
