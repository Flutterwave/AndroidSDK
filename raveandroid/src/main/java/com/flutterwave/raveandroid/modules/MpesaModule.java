package com.flutterwave.raveandroid.modules;

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

    public MpesaModule(Context context) {
        this.context = context;
    }

    public MpesaModule(MpesaContract.View view) {
        this.view = view;
    }

    @Inject
    public MpesaModule(Context context, MpesaContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Singleton
    @Provides
    public Context providesGetContext() {
        return context;
    }

    @Singleton
    @Provides
    public MpesaContract.View providesContract() {
        return view;
    }
}
