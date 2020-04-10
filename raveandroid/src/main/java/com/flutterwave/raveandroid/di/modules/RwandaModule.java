package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class RwandaModule {

    private RwfMobileMoneyUiContract.View view;

    @Inject
    public RwandaModule(RwfMobileMoneyUiContract.View view) {
        this.view = view;
    }

    @Provides
    public RwfMobileMoneyUiContract.View providesContract() {
        return view;
    }
}
