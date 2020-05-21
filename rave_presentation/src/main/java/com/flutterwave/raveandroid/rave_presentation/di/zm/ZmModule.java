package com.flutterwave.raveandroid.rave_presentation.di.zm;


import com.flutterwave.raveandroid.rave_presentation.zmmobilemoney.ZmMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class ZmModule {

    private ZmMobileMoneyContract.Interactor interactor;

    @Inject
    public ZmModule(ZmMobileMoneyContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public ZmMobileMoneyContract.Interactor providesContract() {
        return interactor;
    }
}
