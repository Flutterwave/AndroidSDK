package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.mpesa.MpesaContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class MpesaModule {

    private MpesaContract.View view;

    @Inject
    public MpesaModule(MpesaContract.View view) {
        this.view = view;
    }

    @Provides
    public MpesaContract.View providesContract() {
        return view;
    }
}
