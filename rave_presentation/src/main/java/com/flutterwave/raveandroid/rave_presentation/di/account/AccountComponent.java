package com.flutterwave.raveandroid.rave_presentation.di.account;


import com.flutterwave.raveandroid.rave_presentation.account.AccountPayManager;

import dagger.Subcomponent;

@AccountScope
@Subcomponent(modules = {AccountModule.class})
public interface AccountComponent {
    void inject(AccountPayManager manager);
}
