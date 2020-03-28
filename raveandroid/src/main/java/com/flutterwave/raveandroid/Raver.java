package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.rave_presentation.RavePayInitializer;

public class Raver {

    RavePayInitializer ravePayInitializer;
    RaveUiComponent raveUiComponent;

    public Raver(RavePayInitializer ravePayInitializer, RaveUiComponent raveUiComponent) {
        this.ravePayInitializer = ravePayInitializer;
        this.raveUiComponent = raveUiComponent;
    }

    public RavePayInitializer getRavePayInitializer() {
        return ravePayInitializer;
    }

    public void setRavePayInitializer(RavePayInitializer ravePayInitializer) {
        this.ravePayInitializer = ravePayInitializer;
    }

    public RaveUiComponent getRaveUiComponent() {
        return raveUiComponent;
    }

    public void setRaveUiComponent(RaveUiComponent raveUiComponent) {
        this.raveUiComponent = raveUiComponent;
    }


}
