package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.FrancModule;
import com.flutterwave.raveandroid.di.scopes.FrancScope;
import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyFragment;

import dagger.Subcomponent;

@FrancScope
@Subcomponent(modules = {FrancModule.class})
public interface FrancComponent {
    void inject(FrancMobileMoneyFragment francMobileMoneyFragment);
}
