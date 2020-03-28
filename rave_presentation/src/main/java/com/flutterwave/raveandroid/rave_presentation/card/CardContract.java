package com.flutterwave.raveandroid.rave_presentation.card;


import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_presentation.account.AccountContract;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public interface CardContract {

    interface View {
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
         * Typically, you can show a user this list of their saved cards, and then call {@link UserActionsListener#chargeSavedCard(Payload, SavedCard, String)} with any of the cards the user chooses.
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
         * Called when the call to {@link UserActionsListener#fetchFee(Payload) get the applicable transaction fee} has been completed successfully.
         *
         * @param chargeAmount The total charge amount (fee inclusive)
         * @param payload      The payload used to initiate the fee request
         */
        void onTransactionFeeFetched(String chargeAmount, Payload payload);

        /**
         * Called when there is an error while fetching the transaction Fee.
         *
         * @param errorMessage The error message that can be displayed to the user
         */
        void onFetchFeeError(String errorMessage);

        /**
         * Called when the card used requires PIN authentication.
         * Collect the PIN from the user and then continue the charge by calling {@link UserActionsListener#chargeCardWithPinAuthModel(Payload, String, String)} with that PIN and your encryption key.
         *
         * @param payload Payload with the charge details
         */
        void collectCardPin(Payload payload);

        /**
         * Called to collect an OTP from the user.
         * After user inputs OTP, call {@link UserActionsListener#validateCardCharge(String, String, String)}
         * with the OTP collected, to continue the transaction.
         *
         * @param flwRef  The Flutterwave transaction reference
         * @param message Instruction message to be shown to the user
         */
        void collectOtp(String flwRef, String message);

        /**
         * Called to collect an OTP from the user for saved card charge.
         * After user inputs OTP, add it to the payload using {@link Payload#setOtp(String)}
         * and call {@link UserActionsListener#chargeSavedCard(Payload, SavedCard, String)} to continue the transaction.
         *
         * @param payload Payload containing charge details
         */
        void collectOtpForSaveCardCharge(Payload payload);

        /**
         * Called when the card used requires Address Verification.
         * Collect the {@link AddressDetails} from the user and then continue the charge by calling {@link UserActionsListener#chargeCardWithAddressDetails(Payload, AddressDetails, String, String)} with that address.
         *
         * @param payload   Payload with the charge details
         * @param authModel Authentication Model to be passed to the {@link UserActionsListener#chargeCardWithAddressDetails(Payload, AddressDetails, String, String)} function.
         */
        void collectCardAddressDetails(Payload payload, String authModel);

        /**
         * Called to display a {@link android.webkit.WebView} for cards that require 3D-Secure authentication.
         * When the payment is completed, the authentication page redirects to a {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined url}
         * with the payment details appended to the url.
         * <p>
         * You should override the webview client's {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)}  shouldOverrideUrlLoading}
         * function to check if the {@link WebResourceRequest#getUrl() url being loaded} contains the
         * {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined redirect url}.
         * <p>
         * If it does, it means the transaction has been completed and you can now call {@link UserActionsListener#requeryTx(String, String)} with the {@code flwRef} to check the transaction status.
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

    interface UserActionsListener {

        /**
         * Detaches the view from the presenter.
         * Call this in activity or fragment onStop() function.
         */
        void onDetachView();

        /**
         * Save a card for future transactions to eliminate having to type in card number repeatedly.
         * Note that this is different from <a href="https://developer.flutterwave.com/v2.0/reference#save-a-card">Card Tokenization</a>.
         * For this type of saved card, your user still needs to authenticate with an OTP that will be sent to their phone number.
         *
         * @param phoneNumber User's phone number
         * @param email       User's email address
         * @param FlwRef      Flutterwave reference for a successful transaction done with the card to be saved
         * @param publicKey   Your public key
         */
        void saveCardToRave(String phoneNumber, String email, String FlwRef, String publicKey);

        /**
         * Check for the fee applicable for this transaction.
         *
         * @param payload Object containing the charge details.
         *                Can be generated with the {@link PayloadBuilder PayloadBuilder}
         */
        void fetchFee(Payload payload);

        /**
         * Reattaches the view to the presenter.
         * Call this in activity or fragment onStart() function.
         *
         * @param view View to be attached
         */
        void onAttachView(CardContract.View view);

        /**
         * Initiate the card charge.
         * This is the starting point for the payment.
         *
         * @param payload       Object containing the charge details.
         *                      Can be generated with the {@link PayloadBuilder PayloadBuilder}
         * @param encryptionKey Your Flutterwave encryption key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void chargeCard(Payload payload, String encryptionKey);

        /**
         * Authenticate a payment with OTP gotten from the customer.
         *
         * @param flwRef    The Flutterwave reference for the transaction
         * @param otp       OTP gotten from the user
         * @param publicKey Your Flutterwave public key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void validateCardCharge(String flwRef, String otp, String publicKey);

        /**
         * Check for a transactions status. Result is sent to either {@link AccountContract.View#onPaymentSuccessful(String)}
         * when it's successful, or {@link View#onPaymentFailed(String, String)} (String)} if it's failed.
         *
         * @param flwRef    The Flutterwave reference for the transaction
         * @param publicKey The public Key used to initiate the transaction
         */
        void requeryTx(String flwRef, String publicKey);

        /**
         * Continue a charge after the OTP has been gotten from the user
         *
         * @param payload       Payload object containing charge details
         * @param pin           Card PIN gotten from the user
         * @param encryptionKey Your Flutterwave encryption key.
         *                      Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void chargeCardWithPinAuthModel(Payload payload, String pin, String encryptionKey);

        /**
         * Check for any saved cards associated with the user with this {@param phoneNumber}.
         *
         * @param publicKey   Your Flutterwave public key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         * @param phoneNumber
         */
        void lookupSavedCards(String publicKey, String phoneNumber);

        /**
         * Charge a {@link SavedCard}
         *
         * @param payload       Payload object containing charge details
         * @param savedCard     Saved card to charge
         * @param encryptionKey Your Flutterwave encryption key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void chargeSavedCard(Payload payload, SavedCard savedCard, String encryptionKey);

        void logEvent(Event event, String publicKey);

        /**
         * Continue a charge after the details of the card address have been gotten from the user
         *
         * @param payLoad       Payload object containing charge details
         * @param address       Card address details
         * @param encryptionKey Your Flutterwave encryption key.
         *                      Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         * @param authModel     Auth Model passed to the {@link View#collectCardAddressDetails(Payload, String)} method.
         */
        void chargeCardWithAddressDetails(Payload payLoad, AddressDetails address, String encryptionKey, String authModel);
    }

}
