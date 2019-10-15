package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.uk.UkContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UkModule {

    private UkContract.View view;

    @Inject
    public UkModule(UkContract.View view) {
        this.view = view;
    }

    @Provides
    public UkContract.View providesContract() {
        return view;
    }
}
