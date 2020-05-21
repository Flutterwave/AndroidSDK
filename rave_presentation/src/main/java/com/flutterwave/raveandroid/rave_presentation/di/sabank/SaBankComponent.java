package com.flutterwave.raveandroid.rave_presentation.di.sabank;


import com.flutterwave.raveandroid.rave_presentation.sabankaccount.SaBankAccountPaymentManager;

import dagger.Subcomponent;

@SaBankScope
@Subcomponent(modules = {SaBankModule.class})
public interface SaBankComponent {
    void inject(SaBankAccountPaymentManager manager);
}
