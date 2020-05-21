package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.UssdModule;
import com.flutterwave.raveandroid.di.scopes.UssdScope;
import com.flutterwave.raveandroid.ussd.UssdFragment;

import dagger.Subcomponent;

@UssdScope
@Subcomponent(modules = {UssdModule.class})
public interface UssdComponent {
    void inject(UssdFragment ussdFragment);
}