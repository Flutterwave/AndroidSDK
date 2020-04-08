package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import android.support.annotation.Nullable;

public interface FrancophonePaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
