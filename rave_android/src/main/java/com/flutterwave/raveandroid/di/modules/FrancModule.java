package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class FrancModule {

    private FrancMobileMoneyUiContract.View view;

    @Inject
    public FrancModule(FrancMobileMoneyUiContract.View view) {
        this.view = view;
    }

    @Provides
    public FrancMobileMoneyUiContract.View providesContract() {
        return view;
    }
}
