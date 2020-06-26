package com.flutterwave.raveandroid.rave_core.di;

import com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class DeviceIdGetterModule {
    String deviceId;

    @Inject
    public DeviceIdGetterModule(String deviceId) {
        this.deviceId = deviceId;
    }

    @Provides
    public DeviceIdGetter provideDeviceIdGetter(){
        return new DeviceIdGetter(deviceId);
    }
}
