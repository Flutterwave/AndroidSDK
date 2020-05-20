package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class GhanaModule {

    private GhMobileMoneyUiContract.View view;

    @Inject
    public GhanaModule(GhMobileMoneyUiContract.View view) {
        this.view = view;
    }

    @Provides
    public GhMobileMoneyUiContract.View providesContract() {
        return view;
    }
}
