package com.flutterwave.raveandroid.rave_presentation.card;


import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

public interface CardContract {

    interface CardInteractor {
        /**
         * Called to indicate that a network call has just begun or has just ended.
         * E.g., by showing or hiding a progress bar
         *
         * @param active This is true if the progress bar should be shown,
         *               and false if it should be hidden
         */
        void showProgressIndicator(boolean active);

        /**
         * Called when the list of saved cards has been retrieved.
         * Typically, you can show a user this list of their saved cards, and then call {@link CardPaymentHandler#chargeSavedCard(Payload, String)} with any of the cards the user chooses.
         *
         * @param cards       List of user's saved cards
         * @param phoneNumber Phone number against which they were saved
         */
        void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber);

        /**
         * Called when an the saved cards retrieval fails
         *
         * @param message The error message
         */
        void onSavedCardsLookupFailed(String message);


        /**
         * Called when deleting saved cards retrieval is successful
         */
        void onSavedCardRemoveSuccessful();

        /**
         * Called when deleting saved cards retrieval fails
         *
         * @param message The error message
         */
        void onSavedCardRemoveFailed(String message);

        /**
         * Called when the call to {@link CardPaymentHandler#fetchFee(Payload) get the applicable transaction fee} has been completed successfully.
         *
         * @param chargeAmount The total charge amount (fee inclusive)
         * @param payload      The payload used to initiate the fee request
         */
        void onTransactionFeeFetched(String chargeAmount, Payload payload, String fee);

        /**
         * Called when there is an error while fetching the transaction Fee.
         *
         * @param errorMessage The error message that can be displayed to the user
         */
        void onFetchFeeError(String errorMessage);

        /**
         * Called when the card used requires PIN authentication.
         * Collect the PIN from the user and then continue the charge by calling {@link CardPaymentHandler#chargeCardWithPinAuthModel(Payload, String, String)} with that PIN and your encryption key.
         *
         * @param payload Payload with the charge details
         */
        void collectCardPin(Payload payload);

        /**
         * Called to collect an OTP from the user.
         * After user inputs OTP, call {@link CardPaymentHandler#validateCardCharge(String, String, String)}
         * with the OTP collected, to continue the transaction.
         *
         * @param flwRef  The Flutterwave transaction reference
         * @param message Instruction message to be shown to the user
         */
        void collectOtp(String flwRef, String message);

        /**
         * Called to collect an OTP from the user for saved card charge.
         * After user inputs OTP, add it to the payload using {@link Payload#setOtp(String)}
         * and call {@link CardPaymentHandler#chargeSavedCard(Payload, String)} to continue the transaction.
         *
         * @param payload Payload containing charge details
         */
        void collectOtpForSaveCardCharge(Payload payload);

        /**
         * Called when the card used requires Address Verification.
         * Collect the {@link AddressDetails} from the user and then continue the charge by calling {@link CardPaymentHandler#chargeCardWithAddressDetails(Payload, AddressDetails, String, String)} with that address.
         *
         * @param payload   Payload with the charge details
         * @param authModel Authentication Model to be passed to the {@link CardPaymentHandler#chargeCardWithAddressDetails(Payload, AddressDetails, String, String)} function.
         */
        void collectCardAddressDetails(Payload payload, String authModel);

        /**
         * Called to display a {@link android.webkit.WebView} for charges that require webpage authentication.
         * When the payment is completed, the authentication page redirects to a {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined url}
         * with the payment details appended to the url.
         * <p>
         * You should override the webview client's {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)  shouldOverrideUrlLoading}
         * function to check if the {@link WebResourceRequest#getUrl() url being loaded} contains the
         * {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined redirect url}.
         * <p>
         * If it does, it means the transaction has been completed and you can now call {@link CardPaymentHandler#requeryTx(String, String)} with the {@code flwRef} to check the transaction status.
         *
         * @param authenticationUrl The url to the authentication page
         * @param flwRef            The Flutterwave transaction reference
         */
        void showWebPage(String authenticationUrl, String flwRef);

        /**
         * Called when the payment has been completed successfully.
         *
         * @param responseAsJSONString The response containing the transaction details in JSON format
         */
        void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString);

        /**
         * Called when there is an error during the payment attempt.
         *
         * @param errorMessage The error message that can be displayed to the user
         */
        void onPaymentError(String errorMessage);

        /**
         * Called when the payment attempt is complete, but the payment failed.
         *
         * @param responseAsJsonString The response containing the transaction details in JSON format
         */
        void onPaymentFailed(String status, String responseAsJsonString);

        /**
         * Called when a card has been successfully saved
         */
        void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber);

        /**
         * Called when an attempt to save a card fails
         *
         * @param message The error message
         */
        void onCardSaveFailed(String message);
    }

    interface CardPaymentHandler {
        void saveCardToRave(String phoneNumber, String email, String FlwRef, String publicKey);

        void fetchFee(Payload payload);

        void chargeCard(Payload payload, String encryptionKey);

        void checkCard(String cardFirstSix, Payload body, Boolean isDisplayFee, String encryptionKey, String barterCountry);

        void validateCardCharge(String flwRef, String otp, String publicKey);

        void requeryTx(String flwRef, String publicKey);

        void chargeCardWithPinAuthModel(Payload payload, String pin, String encryptionKey);

        void lookupSavedCards(String publicKey, String phoneNumber, boolean showLoader);

        void deleteASavedCard(String cardHash, String phoneNumber, String publicKey);

        void chargeSavedCard(Payload payload, String encryptionKey);

        void logEvent(Event event, String publicKey);

        void chargeCardWithAddressDetails(Payload payLoad, AddressDetails address, String encryptionKey, String authModel);
    }

}
