package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyContract;
import com.flutterwave.raveandroid.sabankaccount.SaBankAccountContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class SaBankModule {
    private SaBankAccountContract.View view;

    @Inject
    public SaBankModule(SaBankAccountContract.View view) {
        this.view = view;
    }

    @Provides
    public SaBankAccountContract.View providesContract() {
        return view;
    }
}
