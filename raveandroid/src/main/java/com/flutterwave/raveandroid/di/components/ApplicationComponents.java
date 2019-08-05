package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MpesaModule.class, NetworkModule.class})
public interface ApplicationComponents {
    void inject(MpesaFragment mpesaFragment);
}

