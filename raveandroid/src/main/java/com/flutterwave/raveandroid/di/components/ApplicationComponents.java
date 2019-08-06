package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.AppModule;
import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.di.modules.UgandaModule;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface ApplicationComponents {

    @Singleton
    @Component(modules = {AppModule.class, MpesaModule.class, NetworkModule.class})
    interface MpesaComponents {
        void inject(MpesaFragment mpesaFragment);
    }

    @Singleton
    @Component(modules = {AppModule.class, UgandaModule.class, NetworkModule.class})
    interface UgandaComponents {
        void inject(UgMobileMoneyFragment ugMobileMoneyFragment);
    }
}

