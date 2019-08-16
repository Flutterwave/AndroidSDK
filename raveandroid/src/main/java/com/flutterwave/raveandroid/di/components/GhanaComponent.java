package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.GhanaModule;
import com.flutterwave.raveandroid.di.scopes.GhanaScope;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyFragment;

import dagger.Subcomponent;

@GhanaScope
@Subcomponent(modules = {GhanaModule.class})
public interface GhanaComponent {
    void inject(GhMobileMoneyFragment ghMobileMoneyFragment);
}
