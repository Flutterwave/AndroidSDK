package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;

import java.util.List;

public interface SavedCardsListener {

    /**
     * Called when the users saved cards list has been retrieved successfully.
     *
     * @param cards       A list of the user's saved cards
     * @param phoneNumber The user phone number associated with these saved cards
     */
    void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber);

    /**
     * Called when the saved card lookup fails.
     *
     * @param message Error message
     */
    void onSavedCardsLookupFailed(String message);

    /**
     * Called when a request to delete a card is successful.
     */
    void onDeleteSavedCardRequestSuccessful();

    /**
     * Called when a request to delete a card fails.
     *
     * @param message
     */
    void onDeleteSavedCardRequestFailed(String message);

    /**
     * Called when an OTP is needed to complete a card charge. The OTP should be collected from the
     * user and submitted using {@link CardPaymentManager#submitOtp(String)}
     */
    void collectOtpForSaveCardCharge();

    /**
     * Called when a card has been saved successfully.
     *
     * @param phoneNumber The user phone number against which the card was saved.
     */
    void onCardSaveSuccessful(String phoneNumber);

    /**
     * Called when a card  save request fails.
     *
     * @param message Error message
     */
    void onCardSaveFailed(String message);

}
