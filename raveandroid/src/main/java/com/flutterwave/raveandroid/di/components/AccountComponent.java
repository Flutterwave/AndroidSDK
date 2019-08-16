package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.account.AccountFragment;
import com.flutterwave.raveandroid.di.modules.AccountModule;
import com.flutterwave.raveandroid.di.scopes.AccountScope;

import dagger.Subcomponent;

@AccountScope
@Subcomponent(modules = {AccountModule.class})
public interface AccountComponent {
    void inject(AccountFragment accountFragment);
}
