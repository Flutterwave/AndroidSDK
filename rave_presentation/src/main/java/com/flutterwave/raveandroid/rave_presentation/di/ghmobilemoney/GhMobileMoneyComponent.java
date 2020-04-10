package com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhanaMobileMoneyManager;

import dagger.Subcomponent;

@GhMobileMoneyScope
@Subcomponent(modules = {GhMobileMoneyModule.class})
public interface GhMobileMoneyComponent {
    void inject(GhanaMobileMoneyManager manager);
}
