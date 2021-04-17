package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_presentation.card.CardContract;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface FrancMobileMoneyContract {

    interface Interactor {
        void showFetchFeeFailed(String s);

        /**
         * Called to display a {@link android.webkit.WebView} for charges that require webpage authentication.
         * When the payment is completed, the authentication page redirects to a {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined url}
         * with the payment details appended to the url.
         * <p>
         * You should override the webview client's {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)  shouldOverrideUrlLoading}
         * function to check if the {@link WebResourceRequest#getUrl() url being loaded} contains the
         * {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined redirect url}.
         * <p>
         * If it does, it means the transaction has been completed and you can now call {@link CardContract.CardPaymentHandler#requeryTx(String, String)} with the {@code flwRef} to check the transaction status.
         *
         * @param authenticationUrl The url to the authentication page
         * @param flwRef            The Flutterwave transaction reference
         */
        void showWebPage(String authenticationUrl, String flwRef);

        void onPaymentError(String message);

        void showPollingIndicator(boolean active, String note);

        void showProgressIndicator(boolean active);

        void onTransactionFeeRetrieved(String charge_amount, Payload payload, String fee);

        void onPaymentFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful( String flwRef, String responseAsString);
    }

    interface Handler {
        void fetchFee(Payload payload);

        void chargeFranc(Payload payload, String encryptionKey);

        void requeryTx(String flwRef, String publicKey, String note);

        void logEvent(Event event, String publicKey);
    }
}
