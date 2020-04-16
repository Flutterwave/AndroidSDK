package com.flutterwave.raveandroid.rave_presentation.ussd;

import android.support.annotation.Nullable;

public interface UssdPaymentCallback {
    void showProgressIndicator(boolean active);

    void onError(String errorMessage, @Nullable String flwRef);

    void onSuccessful(String flwRef);

    void onPollingTimeout(String flwRef);

    void onUssdDetailsReceived(String ussdCode, String referenceCode);
}
