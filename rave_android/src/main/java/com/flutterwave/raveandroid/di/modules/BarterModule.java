package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.barter.BarterUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;


@Module
public class BarterModule {

    private BarterUiContract.View view;

    @Inject
    public BarterModule(BarterUiContract.View view) {
        this.view = view;
    }

    @Provides
    public BarterUiContract.View providesContract() {
        return view;
    }
}
