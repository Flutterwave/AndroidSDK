package com.flutterwave.raveandroid.rave_cache.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.flutterwave.raveandroid.rave_core.di.UiScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CacheModule {

    private String RAVE_PAY = "ravepay";

    @Provides
    @UiScope
    public SharedPreferences providesSharedPreferences(Context context) {
        return context.getSharedPreferences(
                RAVE_PAY, Context.MODE_PRIVATE);
    }
}
