package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.ZambiaModule;
import com.flutterwave.raveandroid.di.scopes.ZambiaScope;
import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyFragment;

import dagger.Subcomponent;

@ZambiaScope
@Subcomponent(modules = {ZambiaModule.class})
public interface ZambiaComponent {
    void inject(ZmMobileMoneyFragment zmMobileMoneyFragment);
}
