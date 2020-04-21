package com.flutterwave.raveandroid.rave_presentation.di.ach;


import com.flutterwave.raveandroid.rave_presentation.ach.AchPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardScope;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {AchModule.class})
public interface AchComponent {
    void inject(AchPaymentManager cardPayManager);
}
