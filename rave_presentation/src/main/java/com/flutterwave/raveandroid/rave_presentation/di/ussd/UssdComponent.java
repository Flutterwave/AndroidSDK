package com.flutterwave.raveandroid.rave_presentation.di.ussd;


import com.flutterwave.raveandroid.rave_presentation.ussd.UssdPaymentManager;

import dagger.Subcomponent;

@UssdScope
@Subcomponent(modules = {UssdModule.class})
public interface UssdComponent {
    void inject(UssdPaymentManager manager);
}
