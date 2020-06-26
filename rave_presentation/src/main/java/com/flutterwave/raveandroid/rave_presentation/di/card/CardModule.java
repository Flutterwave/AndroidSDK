package com.flutterwave.raveandroid.rave_presentation.di.card;


import com.flutterwave.raveandroid.rave_presentation.card.CardContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class CardModule {

    private CardContract.CardInteractor cardInteractor;

    @Inject
    public CardModule(CardContract.CardInteractor cardInteractor) {
        this.cardInteractor = cardInteractor;
    }

    @Provides
    public CardContract.CardInteractor providesContract() {
        return cardInteractor;
    }
}
