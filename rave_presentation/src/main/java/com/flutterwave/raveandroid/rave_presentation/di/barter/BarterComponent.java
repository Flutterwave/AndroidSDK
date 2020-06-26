package com.flutterwave.raveandroid.rave_presentation.di.barter;


import com.flutterwave.raveandroid.rave_presentation.barter.BarterPaymentManager;

import dagger.Subcomponent;

@BarterScope
@Subcomponent(modules = {BarterModule.class})
public interface BarterComponent {
    void inject(BarterPaymentManager cardPayManager);
}
