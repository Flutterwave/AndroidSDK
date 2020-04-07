package com.flutterwave.raveandroid.rave_presentation.di.ach;


import com.flutterwave.raveandroid.rave_presentation.ach.AchPayManager;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardScope;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {AchModule.class})
public interface AchComponent {
    void inject(AchPayManager cardPayManager);
}
