package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UgandaModule {

    private UgMobileMoneyUiContract.View view;

    @Inject
    public UgandaModule(UgMobileMoneyUiContract.View view) {
        this.view = view;
    }

    @Provides
    public UgMobileMoneyUiContract.View providesContract() {
        return view;
    }
}
