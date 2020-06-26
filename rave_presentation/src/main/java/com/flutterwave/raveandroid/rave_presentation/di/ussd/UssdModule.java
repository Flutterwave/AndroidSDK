package com.flutterwave.raveandroid.rave_presentation.di.ussd;


import com.flutterwave.raveandroid.rave_presentation.ussd.UssdContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UssdModule {

    private UssdContract.Interactor interactor;

    @Inject
    public UssdModule(UssdContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public UssdContract.Interactor providesContract() {
        return interactor;
    }
}
