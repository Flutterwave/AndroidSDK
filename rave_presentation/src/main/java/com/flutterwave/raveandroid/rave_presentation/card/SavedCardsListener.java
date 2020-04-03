package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

public interface SavedCardsListener {

    void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber);

    void onSavedCardsLookupFailed(String message);

    void collectOtpForSaveCardCharge();

    void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber);

    void onCardSaveFailed(String message);

}
