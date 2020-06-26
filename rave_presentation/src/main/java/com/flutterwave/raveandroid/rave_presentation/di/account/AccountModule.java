package com.flutterwave.raveandroid.rave_presentation.di.account;


import com.flutterwave.raveandroid.rave_presentation.account.AccountContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

    private AccountContract.AccountInteractor interactor;

    @Inject
    public AccountModule(AccountContract.AccountInteractor AccountInteractor) {
        this.interactor = AccountInteractor;
    }

    @Provides
    public AccountContract.AccountInteractor providesContract() {
        return interactor;
    }

}
