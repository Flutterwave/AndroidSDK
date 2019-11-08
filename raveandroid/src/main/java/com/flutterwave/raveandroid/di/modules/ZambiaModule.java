package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class ZambiaModule {

    private ZmMobileMoneyContract.View view;

    @Inject
    public ZambiaModule(ZmMobileMoneyContract.View view) {
        this.view = view;
    }

    @Provides
    public ZmMobileMoneyContract.View providesContract() {
        return view;
    }
}
