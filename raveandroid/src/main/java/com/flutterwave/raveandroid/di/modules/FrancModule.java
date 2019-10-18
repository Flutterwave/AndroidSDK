package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class FrancModule {

    private FrancMobileMoneyContract.View view;

    @Inject
    public FrancModule(FrancMobileMoneyContract.View view) {
        this.view = view;
    }

    @Provides
    public FrancMobileMoneyContract.View providesContract() {
        return view;
    }
}
