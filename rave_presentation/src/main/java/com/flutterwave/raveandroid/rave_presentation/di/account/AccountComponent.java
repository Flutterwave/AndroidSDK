package com.flutterwave.raveandroid.rave_presentation.di.account;


import com.flutterwave.raveandroid.rave_presentation.account.AccountPaymentManager;

import dagger.Subcomponent;

@AccountScope
@Subcomponent(modules = {AccountModule.class})
public interface AccountComponent {
    void inject(AccountPaymentManager manager);
}
