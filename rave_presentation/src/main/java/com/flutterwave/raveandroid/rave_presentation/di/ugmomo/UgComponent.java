package com.flutterwave.raveandroid.rave_presentation.di.ugmomo;


import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyManager;

import dagger.Subcomponent;

@UgScope
@Subcomponent(modules = {UgModule.class})
public interface UgComponent {
    void inject(UgandaMobileMoneyManager manager);
}
