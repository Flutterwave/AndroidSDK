package com.flutterwave.raveandroid.rave_presentation.di.acquireddotcom;


import com.flutterwave.raveandroid.rave_presentation.acquireddotcom.AcquiredContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AcquiredModule {

    private AcquiredContract.Interactor interactor;

    @Inject
    public AcquiredModule(AcquiredContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public AcquiredContract.Interactor providesContract() {
        return interactor;
    }
}
