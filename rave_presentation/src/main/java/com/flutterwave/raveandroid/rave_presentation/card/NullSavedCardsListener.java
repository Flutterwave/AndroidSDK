package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

class NullSavedCardsListener implements SavedCardsListener {
    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {

    }

    @Override
    public void onSavedCardsLookupFailed(String message) {

    }

    @Override
    public void collectOtpForSaveCardCharge() {

    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }
}
