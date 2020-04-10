package com.flutterwave.raveandroid.rave_presentation.sabankaccount;

import android.support.annotation.Nullable;

public interface SaBankAccountCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void showAuthenticationWebPage(String authUrl);
}
