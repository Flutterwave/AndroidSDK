package com.flutterwave.raveandroid;

import android.app.Activity;
import android.app.Application;

import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;

public class RaveApp extends Application {

    AppComponent appComponent;
    Activity activity;

    public String baseUrl;

    public RaveApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppComponent getAppComponent() {
        baseUrl = ((RavePayActivity) activity).getBaseUrl();
        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .networkModule(new NetworkModule(baseUrl))
                .build();
        appComponent.inject(this);
        return appComponent;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.activity = mCurrentActivity;
    }

}
