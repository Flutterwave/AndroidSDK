package com.flutterwave.raveandroid.di;

import android.content.Context;
import android.os.Bundle;
import android.test.mock.MockContext;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.NetworkValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;

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
    public SharedPrefsRepo providesSharedPrefsRequestImpl() {
        return Mockito.mock(SharedPrefsRepo.class);
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
    public PhoneValidator providesPhoneValidator() {
        return Mockito.mock(PhoneValidator.class);
    }

    @Provides
    @Singleton
    public NetworkValidator providesNetworkValidator() {
        return Mockito.mock(NetworkValidator.class);
    }

    @Provides
    @Singleton
    public DateOfBirthValidator providesDateOfBirthValidator() {
        return Mockito.mock(DateOfBirthValidator.class);
    }

    @Provides
    @Singleton
    public BvnValidator providesBvnValidator() {
        return Mockito.mock(BvnValidator.class);
    }


    @Provides
    @Singleton
    public AccountNoValidator providesAccountNoValidator() {
        return Mockito.mock(AccountNoValidator.class);
    }

    @Provides
    @Singleton
    public BankCodeValidator providesBankCodeValidator() {
        return Mockito.mock(BankCodeValidator.class);
    }

    @Provides
    @Singleton
    public BanksMinimum100AccountPaymentValidator providesBanksMinimum100AccountPaymentValidator() {
        return Mockito.mock(BanksMinimum100AccountPaymentValidator.class);
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
    public UrlValidator providesUrlValidator() {
        return Mockito.mock(UrlValidator.class);
    }

    @Provides
    @Singleton
    public RavePayInitializer providesRavePayInitializer() {
        return Mockito.mock(RavePayInitializer.class);
    }

    @Provides
    @Singleton
    public TransactionStatusChecker providesTransactionStatusChecker() {
        return Mockito.mock(TransactionStatusChecker.class);
    }

    @Provides
    @Singleton
    public DeviceIdGetter providesDeviceIdGetter() {
        return Mockito.mock(DeviceIdGetter.class);
    }

    @Provides
    @Singleton
    public PayloadToJsonConverter providesPayloadToJson() {
        return Mockito.mock(PayloadToJsonConverter.class);
    }

    @Provides
    @Singleton
    public PayloadEncryptor providesGetEncryptedData() {
        return Mockito.mock(PayloadEncryptor.class);
    }

    @Provides
    @Singleton
    public com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter providesCoreDeviceIdGetter() {
        return Mockito.mock(com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter.class);
    }

    @Provides
    @Singleton
    public Bundle providesBundle() {
        return Mockito.mock(Bundle.class);
    }

}
