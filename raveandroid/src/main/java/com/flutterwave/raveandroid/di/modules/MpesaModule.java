package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.mpesa.MpesaUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class MpesaModule {

    private MpesaUiContract.View view;

    @Inject
    public MpesaModule(MpesaUiContract.View view) {
        this.view = view;
    }

    @Provides
    public MpesaUiContract.View providesContract() {
        return view;
    }
}
