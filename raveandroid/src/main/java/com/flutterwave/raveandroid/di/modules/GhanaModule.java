package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class GhanaModule {

    private GhMobileMoneyContract.View view;

    @Inject
    public GhanaModule(GhMobileMoneyContract.View view) {
        this.view = view;
    }

    @Provides
    public GhMobileMoneyContract.View providesContract() {
        return view;
    }
}
