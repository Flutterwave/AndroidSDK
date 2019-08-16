package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.account.AccountContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

    private AccountContract.View view;


    @Inject
    public AccountModule(AccountContract.View view) {
        this.view = view;
    }

    @Provides
    public AccountContract.View providesContract() {
        return view;
    }
}
