package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.banktransfer.BankTransferFragment;
import com.flutterwave.raveandroid.di.modules.BankTransferModule;
import com.flutterwave.raveandroid.di.scopes.BankTransferScope;

import dagger.Subcomponent;

@BankTransferScope
@Subcomponent(modules = {BankTransferModule.class})
public interface BankTransferComponent {
    void inject(BankTransferFragment bankTransferFragment);
}
