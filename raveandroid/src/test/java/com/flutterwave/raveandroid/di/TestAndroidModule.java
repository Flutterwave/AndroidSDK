package com.flutterwave.raveandroid.di;

import android.content.Context;
import android.test.mock.MockContext;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CardNoValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestAndroidModule {

    @Provides
    @Singleton
    public Context providesContext() {
        return new MockContext();
    }

    @Provides
    @Singleton
    public AmountValidator providesAmountValidator() {
        return Mockito.mock(AmountValidator.class);
    }

    @Provides
    @Singleton
    public CvvValidator providesCvvValidator() {
        return Mockito.mock(CvvValidator.class);
    }

    @Provides
    @Singleton
    public EmailValidator providesEmailValidator() {
        return Mockito.mock(EmailValidator.class);
    }

    @Provides
    @Singleton
    public CardNoValidator providesCardNoValidator() {
        return Mockito.mock(CardNoValidator.class);
    }

    @Provides
    @Singleton
    public CardExpiryValidator providesCardExpiryValidator() {
        return Mockito.mock(CardExpiryValidator.class);
    }

    @Provides
    @Singleton
    public RavePayInitializer providesRavePayInitializer() {
        return Mockito.mock(RavePayInitializer.class);
    }

    @Provides
    @Singleton
    public DeviceIdGetter providesDeviceIdGetter() {
        return Mockito.mock(DeviceIdGetter.class);
    }

}
