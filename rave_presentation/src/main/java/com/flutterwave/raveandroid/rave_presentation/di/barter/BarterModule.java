package com.flutterwave.raveandroid.rave_presentation.di.barter;


import com.flutterwave.raveandroid.rave_presentation.barter.BarterContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class BarterModule {

    private BarterContract.Interactor interactor;

    @Inject
    public BarterModule(BarterContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public BarterContract.Interactor providesContract() {
        return interactor;
    }
}
