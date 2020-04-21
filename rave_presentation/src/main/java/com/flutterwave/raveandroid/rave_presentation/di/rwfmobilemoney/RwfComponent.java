package com.flutterwave.raveandroid.rave_presentation.di.rwfmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyPaymentManager;

import dagger.Subcomponent;

@RwfScope
@Subcomponent(modules = {RwfModule.class})
public interface RwfComponent {
    void inject(RwfMobileMoneyPaymentManager manager);
}
