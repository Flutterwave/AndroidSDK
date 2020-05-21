package com.flutterwave.raveandroid.di.modules;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.flutterwave.raveandroid.rave_core.di.UiScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private Context context;

    @Inject
    public AndroidModule(Context context) {
        this.context = context;
    }

    @UiScope
    @Provides
    public Context providesContext() {
        return context.getApplicationContext();
    }


    @UiScope
    @Provides
    public TelephonyManager providesTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

}
