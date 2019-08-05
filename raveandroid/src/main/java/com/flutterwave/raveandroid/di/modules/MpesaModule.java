package com.flutterwave.raveandroid.di.modules;

import android.content.Context;

import com.flutterwave.raveandroid.mpesa.MpesaContract;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MpesaModule {

    private Context context;
    private MpesaContract.View view;

    @Inject
    public MpesaModule(Context context, MpesaContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Singleton
    @Provides
    public Context providesContext() {
        return context;
    }

    @Singleton
    @Provides
    public MpesaContract.View providesContract() {
        return view;
    }
}
