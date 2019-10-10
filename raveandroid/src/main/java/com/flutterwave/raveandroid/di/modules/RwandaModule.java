package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class RwandaModule {

    private RwfMobileMoneyContract.View view;

    @Inject
    public RwandaModule(RwfMobileMoneyContract.View view) {
        this.view = view;
    }

    @Provides
    public RwfMobileMoneyContract.View providesContract() {
        return view;
    }
}
