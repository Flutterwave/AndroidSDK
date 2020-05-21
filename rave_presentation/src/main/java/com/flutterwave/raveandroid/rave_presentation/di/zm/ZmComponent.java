package com.flutterwave.raveandroid.rave_presentation.di.zm;


import com.flutterwave.raveandroid.rave_presentation.zmmobilemoney.ZambiaMobileMoneyPaymentManager;

import dagger.Subcomponent;

@ZmScope
@Subcomponent(modules = {ZmModule.class})
public interface ZmComponent {
    void inject(ZambiaMobileMoneyPaymentManager manager);
}
