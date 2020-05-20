package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.ussd.UssdUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class UssdModule {

    private UssdUiContract.View view;

    @Inject
    public UssdModule(UssdUiContract.View view) {
        this.view = view;
    }

    @Provides
    public UssdUiContract.View providesContract() {
        return view;
    }
}
