package com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney;

import androidx.annotation.Nullable;

public interface RwfMobileMoneyPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showAuthenticationWebPage(String url);
}
