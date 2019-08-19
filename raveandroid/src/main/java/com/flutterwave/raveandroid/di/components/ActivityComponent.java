package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class})
public interface ActivityComponent {
    void inject(RavePayActivity ravePayActivity);
}

