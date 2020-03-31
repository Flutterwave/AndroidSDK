package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_core.models.RavePaymentMethods;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

public interface RaveNonUICallback {
    void collectCardPin(RavePaymentMethods ravePaymentMethods);

    void showProgressIndicator(boolean active);

    void collectOtp(RavePaymentMethods ravePaymentMethods, String message);

    void onError(RavePaymentMethods ravePaymentMethods, String errorMessage);

    void onSuccessful(String flwRef);
}
