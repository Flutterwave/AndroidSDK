package com.flutterwave.raveandroid.rave_presentation.di.francmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.francmobilemoney.FrancMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class FrancophoneModule {

    private FrancMobileMoneyContract.Interactor interactor;

    @Inject
    public FrancophoneModule(FrancMobileMoneyContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public FrancMobileMoneyContract.Interactor providesContract() {
        return interactor;
    }
}
