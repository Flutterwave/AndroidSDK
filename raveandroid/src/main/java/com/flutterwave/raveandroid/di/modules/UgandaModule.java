package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UgandaModule {

    private UgMobileMoneyContract.View view;


    @Inject
    public UgandaModule(UgMobileMoneyContract.View view) {
        this.view = view;
    }

    @Provides
    public UgMobileMoneyContract.View providesContract() {
        return view;
    }
}
