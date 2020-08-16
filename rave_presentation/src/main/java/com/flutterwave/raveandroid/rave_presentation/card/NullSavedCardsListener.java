package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;

import java.util.List;

class NullSavedCardsListener implements SavedCardsListener {
    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {

    }

    @Override
    public void onSavedCardsLookupFailed(String message) {

    }

    @Override
    public void onDeleteSavedCardRequestSuccessful() {

    }

    @Override
    public void onDeleteSavedCardRequestFailed(String message) {

    }

    @Override
    public void collectOtpForSaveCardCharge() {

    }

    @Override
    public void onCardSaveSuccessful(String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }
}
