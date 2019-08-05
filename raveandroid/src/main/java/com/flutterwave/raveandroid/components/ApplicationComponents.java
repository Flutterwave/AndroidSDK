package com.flutterwave.raveandroid.components;

import com.flutterwave.raveandroid.modules.MpesaModule;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MpesaModule.class})
public interface ApplicationComponents {
    void inject(MpesaFragment mpesaFragment);
}
