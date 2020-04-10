package com.flutterwave.raveandroid.rave_presentation.di.rwfmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class RwfModule {

    private RwfMobileMoneyContract.Interactor interactor;

    @Inject
    public RwfModule(RwfMobileMoneyContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public RwfMobileMoneyContract.Interactor providesContract() {
        return interactor;
    }
}
