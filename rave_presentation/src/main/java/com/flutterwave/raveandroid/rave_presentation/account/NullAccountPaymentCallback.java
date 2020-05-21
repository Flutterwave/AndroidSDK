package com.flutterwave.raveandroid.rave_presentation.account;

import androidx.annotation.Nullable;

import com.flutterwave.raveandroid.rave_core.models.Bank;

import java.util.List;

public class NullAccountPaymentCallback implements AccountPaymentCallback {
    @Override
    public void onBanksListRetrieved(List<Bank> banks) {

    }

    @Override
    public void onGetBanksRequestFailed(String message) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void collectOtp(String message) {

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
