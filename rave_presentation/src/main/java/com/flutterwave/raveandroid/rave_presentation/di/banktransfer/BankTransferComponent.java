package com.flutterwave.raveandroid.rave_presentation.di.banktransfer;


import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferPaymentManager;

import dagger.Subcomponent;

@BankTransferScope
@Subcomponent(modules = {BankTransferModule.class})
public interface BankTransferComponent {
    void inject(BankTransferPaymentManager manager);
}
