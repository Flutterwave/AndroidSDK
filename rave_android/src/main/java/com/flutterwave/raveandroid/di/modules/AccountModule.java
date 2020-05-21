package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.account.AccountUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

    private AccountUiContract.View view;

    @Inject
    public AccountModule(AccountUiContract.View view) {
        this.view = view;
    }

    @Provides
    public AccountUiContract.View providesContract() {
        return view;
    }
}
