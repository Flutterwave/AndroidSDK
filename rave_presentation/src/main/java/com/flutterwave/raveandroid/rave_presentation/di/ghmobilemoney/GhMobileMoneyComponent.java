package com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.di.card.CardScope;
import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhanaMobileMoneyManager;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {GhMobileMoneyModule.class})
public interface GhMobileMoneyComponent {
    void inject(GhanaMobileMoneyManager manager);
}
