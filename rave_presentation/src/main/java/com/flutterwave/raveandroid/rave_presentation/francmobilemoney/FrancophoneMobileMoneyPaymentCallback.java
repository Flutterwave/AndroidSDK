package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import androidx.annotation.Nullable;

public interface FrancophoneMobileMoneyPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
