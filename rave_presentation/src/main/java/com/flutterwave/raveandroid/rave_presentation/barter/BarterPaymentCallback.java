package com.flutterwave.raveandroid.rave_presentation.barter;

import androidx.annotation.Nullable;

public interface BarterPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void loadBarterCheckout(String authUrlCrude, String flwRef);
}
