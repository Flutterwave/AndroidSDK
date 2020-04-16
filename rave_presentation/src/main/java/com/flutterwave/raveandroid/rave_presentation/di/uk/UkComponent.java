package com.flutterwave.raveandroid.rave_presentation.di.uk;


import com.flutterwave.raveandroid.rave_presentation.di.ugmomo.UgScope;
import com.flutterwave.raveandroid.rave_presentation.uk.UkBankPaymentManager;

import dagger.Subcomponent;

@UgScope
@Subcomponent(modules = {UkModule.class})
public interface UkComponent {
    void inject(UkBankPaymentManager manager);
}
