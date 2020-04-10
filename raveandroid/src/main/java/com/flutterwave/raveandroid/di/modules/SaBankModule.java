package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.sabankaccount.SaBankAccountUiContract;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class SaBankModule {
    private SaBankAccountUiContract.View view;

    @Inject
    public SaBankModule(SaBankAccountUiContract.View view) {
        this.view = view;
    }

    @Provides
    public SaBankAccountUiContract.View providesContract() {
        return view;
    }
}
