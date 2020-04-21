package com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhanaMobileMoneyPaymentManager;

import dagger.Subcomponent;

@GhMobileMoneyScope
@Subcomponent(modules = {GhMobileMoneyModule.class})
public interface GhMobileMoneyComponent {
    void inject(GhanaMobileMoneyPaymentManager manager);
}
