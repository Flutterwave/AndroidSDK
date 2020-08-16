package com.flutterwave.raveandroid.rave_presentation.card;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;

public interface CardPaymentCallback {
    /**
     * Called to indicate that a background task is running, e.g. a network call.
     * This should typically show or hide a progress bar.
     *
     * @param active If true, background task is running. If false, background task has stopped
     */
    void showProgressIndicator(boolean active);

    /**
     * Called to trigger a pin collection. The pin should be collected from the user and passed to
     * the CardPaymentManager using {@link CardPaymentManager#submitPin(String)} to continue the payment.
     */
    void collectCardPin();

    /**
     * Called to trigger an otp collection. The OTP should be collected from the user and passed to
     * the CardPaymentManager using {@link CardPaymentManager#submitOtp(String)} to continue the payment.
     */
    void collectOtp(String message);

    /**
     * Called to trigger address details collection. The address should be collected from the user and passed to
     * the CardPaymentManager using {@link CardPaymentManager#submitAddress(AddressDetails)} to continue the payment.
     */
    void collectAddress();

    /**
     * Called to display a {@link android.webkit.WebView} for charges that require webpage authentication.
     * When the payment is completed, the authentication page redirects to a {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined url}
     * with the payment details appended to the url.
     * <p>
     * You should override the webview client's {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)} shouldOverrideUrlLoading}
     * function to check if the {@link WebResourceRequest#getUrl() url being loaded} contains the
     * {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined redirect url}.
     * <p>
     * If it does, it means the transaction has been completed and you can now call {@link CardPaymentManager#onWebpageAuthenticationComplete()} to check the transaction status.
     *
     * @param authenticationUrl The url to the authentication page
     */
    void showAuthenticationWebPage(String authenticationUrl);

    /**
     * Called when an error occurs with the payment. The error message can be displayed to the users.
     *
     * @param errorMessage A message describing the error
     * @param flwRef       The Flutterwave reference to the transaction.
     */
    void onError(String errorMessage, @Nullable String flwRef);

    /**
     * Called when the transaction has been completed successfully.
     *
     * @param flwRef The Flutterwave reference to the transaction.
     */
    void onSuccessful(String flwRef);
}
