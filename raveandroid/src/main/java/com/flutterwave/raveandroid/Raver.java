package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.di.components.AppComponent;

public class Raver {

    RavePayInitializer ravePayInitializer;
    AppComponent appComponent;

    public Raver(RavePayInitializer ravePayInitializer, AppComponent appComponent) {
        this.ravePayInitializer = ravePayInitializer;
        this.appComponent = appComponent;
    }

    public RavePayInitializer getRavePayInitializer() {
        return ravePayInitializer;
    }

    public void setRavePayInitializer(RavePayInitializer ravePayInitializer) {
        this.ravePayInitializer = ravePayInitializer;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void setAppComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }


}
