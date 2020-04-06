package com.flutterwave.raveandroid.rave_presentation.di.card;


import com.flutterwave.raveandroid.rave_presentation.card.CardPayManager;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {CardModule.class})
public interface CardComponent {
    void inject(CardPayManager cardPayManager);
}
