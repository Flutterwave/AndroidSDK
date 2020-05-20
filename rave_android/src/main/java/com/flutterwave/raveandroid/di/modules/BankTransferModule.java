package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.banktransfer.BankTransferUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class BankTransferModule {

    private BankTransferUiContract.View view;

    @Inject
    public BankTransferModule(BankTransferUiContract.View view) {
        this.view = view;
    }

    @Provides
    public BankTransferUiContract.View providesContract() {
        return view;
    }
}
