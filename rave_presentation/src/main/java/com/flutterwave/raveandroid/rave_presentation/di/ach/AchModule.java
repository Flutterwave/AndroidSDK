package com.flutterwave.raveandroid.rave_presentation.di.ach;


import com.flutterwave.raveandroid.rave_presentation.ach.AchContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AchModule {

    private AchContract.Interactor interactor;

    @Inject
    public AchModule(AchContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public AchContract.Interactor providesContract() {
        return interactor;
    }
}
