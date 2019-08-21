package com.flutterwave.raveandroid.di.modules;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private Context context;

    @Inject
    public AndroidModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context providesContext() {
        return context.getApplicationContext();
    }

}
