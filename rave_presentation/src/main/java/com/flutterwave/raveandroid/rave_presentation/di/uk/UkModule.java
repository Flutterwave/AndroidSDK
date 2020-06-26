package com.flutterwave.raveandroid.rave_presentation.di.uk;


import com.flutterwave.raveandroid.rave_presentation.uk.UkContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UkModule {

    private UkContract.Interactor interactor;

    @Inject
    public UkModule(UkContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public UkContract.Interactor providesContract() {
        return interactor;
    }
}
