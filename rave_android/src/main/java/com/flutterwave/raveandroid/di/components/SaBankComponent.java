package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.SaBankModule;
import com.flutterwave.raveandroid.di.scopes.SaBankScope;
import com.flutterwave.raveandroid.sabankaccount.SaBankAccountFragment;

import dagger.Subcomponent;

@SaBankScope
@Subcomponent(modules = {SaBankModule.class})
public interface SaBankComponent {
    void inject(SaBankAccountFragment saBankAccountFragment);
}
