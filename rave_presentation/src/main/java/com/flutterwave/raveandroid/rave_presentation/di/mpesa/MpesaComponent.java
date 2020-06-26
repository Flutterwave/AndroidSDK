package com.flutterwave.raveandroid.rave_presentation.di.mpesa;


import com.flutterwave.raveandroid.rave_presentation.mpesa.MpesaPaymentManager;

import dagger.Subcomponent;

@MpesaScope
@Subcomponent(modules = {MpesaModule.class})
public interface MpesaComponent {
    void inject(MpesaPaymentManager manager);
}
