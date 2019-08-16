package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.card.CardContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class CardModule {

    private CardContract.View view;

    @Inject
    public CardModule(CardContract.View view) {
        this.view = view;
    }

    @Provides
    public CardContract.View providesContract() {
        return view;
    }
}
