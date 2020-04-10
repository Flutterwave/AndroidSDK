package com.flutterwave.raveandroid.rave_presentation.ghmobilemoney;

import android.support.annotation.Nullable;

public class NullGhMobileMoneyCallback implements GhanaMobileMoneyCallback {

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {

    }

    @Override
    public void onSuccessful(String flwRef) {

    }

}
