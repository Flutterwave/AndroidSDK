package com.flutterwave.raveutils.di;


import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RemoteModule.class, EventLoggerModule.class})
public interface VerificationComponent {


    void inject(AVSVBVFragment avsvbvFragment);

    void inject(OTPFragment otpFragment);

    void inject(PinFragment pinFragment);

    WebComponent plus(WebModule webModule);

}

