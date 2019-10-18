package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ussd.UssdContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UssdModule {

    private UssdContract.View view;

    @Inject
    public UssdModule(UssdContract.View view) {
        this.view = view;
    }

    @Provides
    public UssdContract.View providesContract() {
        return view;
    }
}
