package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.card.CardUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class CardUiModule {

    private CardUiContract.View view;

    @Inject
    public CardUiModule(CardUiContract.View view) {
        this.view = view;
    }

    @Provides
    public CardUiContract.View providesContract() {
        return view;
    }
}
