package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_java_commons.Payload;

public interface CardPaymentCallback {
    void collectCardPin(Payload payload);

    void showProgressIndicator(boolean active);

    void collectOtp(String flwRef, String message);

    void onError(String flwRef, String errorMessage);

    void onError(String errorMessage);

    void onSuccessful(String flwRef);
}
