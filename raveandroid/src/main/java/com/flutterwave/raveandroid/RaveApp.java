package com.flutterwave.raveandroid;

import android.app.Application;

import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;

public class RaveApp extends Application {

    AppComponent appComponent;

    public String baseUrl;

    public RaveApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseUrl = RaveConstants.STAGING_URL;
        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .networkModule(new NetworkModule(baseUrl))
                .build();
        appComponent.inject(this);
    }


}
