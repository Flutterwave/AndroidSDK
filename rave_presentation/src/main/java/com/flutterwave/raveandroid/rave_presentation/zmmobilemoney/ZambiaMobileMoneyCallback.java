package com.flutterwave.raveandroid.rave_presentation.zmmobilemoney;

import android.support.annotation.Nullable;

public interface ZambiaMobileMoneyCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);
}
