package com.flutterwave.raveandroid.rave_presentation.ussd;

import androidx.annotation.Nullable;

public class NullUssdCallback implements UssdPaymentCallback {
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
    public void onPollingTimeout(String flwRef) {

    }

    @Override
    public void onUssdDetailsReceived(String ussdCode, String referenceCode) {

    }
}
