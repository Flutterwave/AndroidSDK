package com.flutterwave.raveandroid.rave_presentation.di.mpesa;


import com.flutterwave.raveandroid.rave_presentation.mpesa.MpesaContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class MpesaModule {

    private MpesaContract.Interactor interactor;

    @Inject
    public MpesaModule(MpesaContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public MpesaContract.Interactor providesContract() {
        return interactor;
    }
}
