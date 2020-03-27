package com.flutterwave.raveandroid.rave_presentation.di;

import android.content.Context;
import android.telephony.TelephonyManager;

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


    @Singleton
    @Provides
    public TelephonyManager providesTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

}
