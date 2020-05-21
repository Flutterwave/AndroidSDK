package com.flutterwave.raveandroid.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import javax.inject.Inject;

public class DeviceIdGetter {

    private Context context;

    @Inject
    public DeviceIdGetter(Context context) {
        this.context = context;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
