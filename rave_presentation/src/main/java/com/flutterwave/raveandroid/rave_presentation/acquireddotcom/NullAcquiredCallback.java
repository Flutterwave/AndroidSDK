package com.flutterwave.raveandroid.rave_presentation.acquireddotcom;

import androidx.annotation.Nullable;

public class NullAcquiredCallback implements AcquiredCallback {
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
    public void showAuthenticationWebPage(String authUrl) {

    }
}
