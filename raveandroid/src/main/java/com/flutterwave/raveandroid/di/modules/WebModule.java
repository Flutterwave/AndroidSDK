package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.verification.web.WebContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;


@Module
public class WebModule {


    private WebContract.View view;

    @Inject
    public WebModule(WebContract.View view) {
        this.view = view;
    }

    @Provides
    public WebContract.View providesContract() {
        return view;
    }
}
