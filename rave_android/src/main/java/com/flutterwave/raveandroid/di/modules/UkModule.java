package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.uk.UkUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UkModule {

    private UkUiContract.View view;

    @Inject
    public UkModule(UkUiContract.View view) {
        this.view = view;
    }

    @Provides
    public UkUiContract.View providesContract() {
        return view;
    }
}
