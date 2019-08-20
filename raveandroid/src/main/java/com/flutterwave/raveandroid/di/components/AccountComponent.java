package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.account.AccountFragment;
import com.flutterwave.raveandroid.di.modules.AccountModule;

import dagger.Subcomponent;

@Subcomponent(modules = {AccountModule.class})
public interface AccountComponent {
    void inject(AccountFragment accountFragment);
}
