package com.flutterwave.raveandroid.rave_presentation.zmmobilemoney;

import androidx.annotation.Nullable;

public interface ZambiaMobileMoneyPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
