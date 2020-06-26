package com.flutterwave.raveandroid.rave_presentation.di.ugmomo;


import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UgModule {

    private UgMobileMoneyContract.Interactor interactor;

    @Inject
    public UgModule(UgMobileMoneyContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public UgMobileMoneyContract.Interactor providesContract() {
        return interactor;
    }
}
