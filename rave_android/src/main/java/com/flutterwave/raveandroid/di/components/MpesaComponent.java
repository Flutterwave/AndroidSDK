package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.scopes.MpesaScope;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;

import dagger.Subcomponent;

@MpesaScope
@Subcomponent(modules = {MpesaModule.class})
public interface MpesaComponent {
    void inject(MpesaFragment mpesaFragment);
}
