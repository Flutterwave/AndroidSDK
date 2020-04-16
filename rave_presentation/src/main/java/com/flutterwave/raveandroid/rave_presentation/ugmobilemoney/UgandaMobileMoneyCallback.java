package com.flutterwave.raveandroid.rave_presentation.ugmobilemoney;

import android.support.annotation.Nullable;

public interface UgandaMobileMoneyCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
