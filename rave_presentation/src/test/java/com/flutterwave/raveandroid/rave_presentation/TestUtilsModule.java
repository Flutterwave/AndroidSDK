package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestUtilsModule {

//    @Provides
//    @Singleton
//    public Context providesContext() {
//        return new MockContext();
//    }

    @Provides
    @Singleton
    public SharedPrefsRepo providesSharedPrefsRequestImpl() {
        return Mockito.mock(SharedPrefsRepo.class);
    }

    //    @Provides
//    @Singleton
//    public CardNoValidator providesCardNoValidator() {
//        return Mockito.mock(CardNoValidator.class);
//    }
//
//    @Provides
//    @Singleton
//    public CardExpiryValidator providesCardExpiryValidator() {
//        return Mockito.mock(CardExpiryValidator.class);
//    }
//
    @Provides
    @Singleton
    public UrlValidator providesUrlValidator() {
        return Mockito.mock(UrlValidator.class);
    }
//
//    @Provides
//    @Singleton
//    public RavePayInitializer providesRavePayInitializer() {
//        return Mockito.mock(RavePayInitializer.class);
//    }

    @Provides
    @Singleton
    public TransactionStatusChecker providesTransactionStatusChecker() {
        return Mockito.mock(TransactionStatusChecker.class);
    }
//
//    @Provides
//    @Singleton
//    public DeviceIdGetter providesDeviceIdGetter() {
//        return Mockito.mock(DeviceIdGetter.class);
//    }

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
//
//    @Provides
//    @Singleton
//    public Bundle providesBundle() {
//        return Mockito.mock(Bundle.class);
//    }

}
