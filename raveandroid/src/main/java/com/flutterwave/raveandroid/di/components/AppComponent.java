package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(AndroidModule androidModule);

    MpesaComponent plus(MpesaModule mpesaModule);
}

