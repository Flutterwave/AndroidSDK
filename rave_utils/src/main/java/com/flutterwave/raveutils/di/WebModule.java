package com.flutterwave.raveutils.di;


import com.flutterwave.raveutils.verification.web.WebContract;

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
