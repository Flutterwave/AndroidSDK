package com.flutterwave.raveandroid.rave_presentation.di.sabank;


import com.flutterwave.raveandroid.rave_presentation.sabankaccount.SaBankAccountContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class SaBankModule {

    private SaBankAccountContract.Interactor interactor;

    @Inject
    public SaBankModule(SaBankAccountContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public SaBankAccountContract.Interactor providesContract() {
        return interactor;
    }
}
