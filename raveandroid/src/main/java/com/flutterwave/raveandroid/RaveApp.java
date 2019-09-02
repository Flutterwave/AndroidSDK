package com.flutterwave.raveandroid;

import android.app.Application;

import com.flutterwave.raveandroid.di.components.AppComponent;

public class RaveApp extends Application {

    AppComponent appComponent;

    public RaveApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void setAppComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }


}
