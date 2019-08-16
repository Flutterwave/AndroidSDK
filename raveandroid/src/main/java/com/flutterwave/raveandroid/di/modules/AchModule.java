package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ach.AchContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AchModule {

    private AchContract.View view;

    @Inject
    public AchModule(AchContract.View view) {
        this.view = view;
    }

    @Provides
    public AchContract.View providesContract() {
        return view;
    }
}
