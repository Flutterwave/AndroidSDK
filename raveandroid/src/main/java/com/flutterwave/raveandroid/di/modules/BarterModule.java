package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.barter.BarterContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;


@Module
public class BarterModule {

    private BarterContract.View view;

    @Inject
    public BarterModule(BarterContract.View view) {
        this.view = view;
    }

    @Provides
    public BarterContract.View providesContract() {
        return view;
    }
}
