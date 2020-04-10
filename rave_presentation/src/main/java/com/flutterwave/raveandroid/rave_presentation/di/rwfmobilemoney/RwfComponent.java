package com.flutterwave.raveandroid.rave_presentation.di.rwfmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyManager;

import dagger.Subcomponent;

@RwfScope
@Subcomponent(modules = {RwfModule.class})
public interface RwfComponent {
    void inject(RwfMobileMoneyManager manager);
}
