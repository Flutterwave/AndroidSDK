package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.RwandaModule;
import com.flutterwave.raveandroid.di.scopes.RwandaScope;
import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyFragment;

import dagger.Subcomponent;

@RwandaScope
@Subcomponent(modules = {RwandaModule.class})
public interface RwandaComponent {
    void inject(RwfMobileMoneyFragment rwfMobileMoneyFragment);
}
