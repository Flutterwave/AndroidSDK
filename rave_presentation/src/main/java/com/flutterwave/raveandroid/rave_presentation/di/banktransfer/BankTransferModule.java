package com.flutterwave.raveandroid.rave_presentation.di.banktransfer;


import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class BankTransferModule {

    private BankTransferContract.BankTransferInteractor interactor;

    @Inject
    public BankTransferModule(BankTransferContract.BankTransferInteractor interactor) {
        this.interactor = interactor;
    }

    @Provides
    public BankTransferContract.BankTransferInteractor providesContract() {
        return interactor;
    }
}
