package com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class GhMobileMoneyModule {

    private GhMobileMoneyContract.Interactor interactor;

    @Inject
    public GhMobileMoneyModule(GhMobileMoneyContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public GhMobileMoneyContract.Interactor providesContract() {
        return interactor;
    }
}
