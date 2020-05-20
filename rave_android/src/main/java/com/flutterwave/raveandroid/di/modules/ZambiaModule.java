package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class ZambiaModule {

    private ZmMobileMoneyUiContract.View view;

    @Inject
    public ZambiaModule(ZmMobileMoneyUiContract.View view) {
        this.view = view;
    }

    @Provides
    public ZmMobileMoneyUiContract.View providesContract() {
        return view;
    }
}
