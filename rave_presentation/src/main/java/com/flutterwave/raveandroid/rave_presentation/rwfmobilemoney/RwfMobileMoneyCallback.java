package com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney;

import android.support.annotation.Nullable;

public interface RwfMobileMoneyCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
