package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ach.AchUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AchModule {

    private AchUiContract.View view;

    @Inject
    public AchModule(AchUiContract.View view) {
        this.view = view;
    }

    @Provides
    public AchUiContract.View providesContract() {
        return view;
    }
}
