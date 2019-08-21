package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.UgandaModule;
import com.flutterwave.raveandroid.di.scopes.UgandaScope;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyFragment;

import dagger.Subcomponent;

@UgandaScope
@Subcomponent(modules = {UgandaModule.class})
public interface UgandaComponent {
    void inject(UgMobileMoneyFragment ugMobileMoneyFragment);
}
