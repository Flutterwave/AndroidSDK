package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_presentation.card.CardContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public interface CardUiContract {

    interface View extends CardContract.CardInteractor {
        void onEmailValidated(String emailToSet, int visibility);

        void onAmountValidated(String amountToSet, int visibility);

        void showSavedCardsLayout(List<SavedCard> savedCardsList);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void onPhoneNumberValidated(String phoneNumber);

        void showCardSavingOption(boolean b);

        void setHasSavedCards(boolean b, List<SavedCard> savedCards);

        void setSavedCardsLayoutVisibility(boolean showPhoneEmailFields);
    }

    interface UserActionsListener extends CardContract.CardPaymentHandler {

        void onDetachView();

        void retrieveSavedCardsFromMemory(String email, String publicKey);

        void onAttachView(CardUiContract.View view);

        void init(RavePayInitializer ravePayInitializer);

        void onSavedCardSwitchSwitchedOn(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void saveCardToSharedPreferences(List<SavedCard> cards, String phoneNumber, String publicKey);

        void checkForSavedCardsInMemory(RavePayInitializer ravePayInitializer);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onDataForSavedCardChargeCollected(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void processSavedCardTransaction(SavedCard savedCard, RavePayInitializer ravePayInitializer);
    }

}
