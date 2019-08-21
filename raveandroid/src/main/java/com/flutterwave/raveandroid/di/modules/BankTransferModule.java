package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.banktransfer.BankTransferContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class BankTransferModule {

    private BankTransferContract.View view;

    @Inject
    public BankTransferModule(BankTransferContract.View view) {
        this.view = view;
    }

    @Provides
    public BankTransferContract.View providesContract() {
        return view;
    }
}
