package com.flutterwave.raveandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import javax.inject.Inject;

public class DeviceIdGetter {

    Context context;
    TelephonyManager telephonyManager;

    @Inject
    public DeviceIdGetter(Context context, TelephonyManager telephonyManager) {
        this.context = context;
        this.telephonyManager = telephonyManager;
    }

    public String getDeviceId() {
        @SuppressLint("MissingPermission") String ip = telephonyManager.getDeviceId();

        if (ip == null) {
            ip = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return ip;
    }
}
