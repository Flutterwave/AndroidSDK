package com.flutterwave.raveandroid.rave_presentation.account;

import androidx.annotation.Nullable;

import com.flutterwave.raveandroid.rave_core.models.Bank;

import java.util.List;

public interface AccountPaymentCallback {
    void onBanksListRetrieved(List<Bank> banks);

    void onGetBanksRequestFailed(String message);

    void showProgressIndicator(boolean active);

    void collectOtp(String message);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showAuthenticationWebPage(String authenticationUrl);
}
