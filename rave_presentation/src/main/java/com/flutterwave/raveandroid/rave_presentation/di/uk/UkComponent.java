package com.flutterwave.raveandroid.rave_presentation.di.uk;


import com.flutterwave.raveandroid.rave_presentation.uk.UkBankPaymentManager;

import dagger.Subcomponent;

@UkScope
@Subcomponent(modules = {UkModule.class})
public interface UkComponent {
    void inject(UkBankPaymentManager manager);
}
