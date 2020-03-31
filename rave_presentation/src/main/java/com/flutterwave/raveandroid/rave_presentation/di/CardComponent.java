package com.flutterwave.raveandroid.rave_presentation.di;


import com.flutterwave.raveandroid.rave_presentation.CardPayManager;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {CardModule.class})
public interface CardComponent {
    void inject(CardPayManager cardPayManager);
}
