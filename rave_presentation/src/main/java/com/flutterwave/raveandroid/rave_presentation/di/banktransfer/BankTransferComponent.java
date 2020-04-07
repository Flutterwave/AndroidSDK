package com.flutterwave.raveandroid.rave_presentation.di.banktransfer;


import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferManager;

import dagger.Subcomponent;

@BankTransferScope
@Subcomponent(modules = {BankTransferModule.class})
public interface BankTransferComponent {
    void inject(BankTransferManager manager);
}
