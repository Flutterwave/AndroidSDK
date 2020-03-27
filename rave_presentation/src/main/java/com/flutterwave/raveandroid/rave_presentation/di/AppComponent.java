package com.flutterwave.raveandroid.rave_presentation.di;


import android.content.Context;

import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_cache.di.CacheModule;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_presentation.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, RemoteModule.class, EventLoggerModule.class, CacheModule.class})
public interface AppComponent {

    RemoteRepository networkImpl();

    EventLogger eventLogger();

    Context getContext();

    //    AmountValidator amountValidator();
//    CvvValidator cvvValidator();
//    EmailValidator emailValidator();
//    DateOfBirthValidator dateOfBirthValidator();
//    BvnValidator bvnValidator();
//    PhoneValidator phoneValidator();
//    CardExpiryValidator cardExpiryValidator();
//    CardNoValidator cardNoValidator();
//    DeviceIdGetter deviceIdGetter();
//    BankCodeValidator bankCodeValidator();
//    AccountNoValidator accountNoValidator();
//    UrlValidator urlValidator();
//    BanksMinimum100AccountPaymentValidator minimum100AccountPaymentValidator();
//    PhoneNumberObfuscator phoneNumberObfuscator();
    PayloadToJsonConverter payloadToJsonConverter();

    //    TransactionStatusChecker transactionStatusChecker();
    PayloadEncryptor payloadEncryptor();
    PayloadToJson payloadToJson();

    SharedPrefsRepo sharedManager();

    //    NetworkValidator networkValidator();
    Gson gson();

//    void inject(RavePayActivity ravePayActivity);
//
//    void inject(AVSVBVFragment avsvbvFragment);
//
//    void inject(OTPFragment otpFragment);
//
//    void inject(PinFragment pinFragment);
//
//    void inject(WebFragment webFragment);
//
//    MpesaComponent plus(MpesaModule mpesaModule);
//
//    UgandaComponent plus(UgandaModule ugandaModule);
//
//    RwandaComponent plus(RwandaModule rwandaModule);
//
//    GhanaComponent plus(GhanaModule ghanaModule);
//
//    ZambiaComponent plus(ZambiaModule zambiaModule);
//
//    CardComponent plus(CardModule cardModule);
//
//    MpesaComponent plus(MpesaModule mpesaModule);
//
//    UgandaComponent plus(UgandaModule ugandaModule);
//
//    RwandaComponent plus(RwandaModule rwandaModule);
//
//    GhanaComponent plus(GhanaModule ghanaModule);
//
//    ZambiaComponent plus(ZambiaModule zambiaModule);
//
//    CardComponent plus(CardModule cardModule);
//
//    BankTransferComponent plus(BankTransferModule bankTransferModule);
//
//    UssdComponent plus(UssdModule ussdModule);
//
//    AccountComponent plus(AccountModule accountModule);
//
//    AchComponent plus(AchModule achModule);
//
//    UkComponent plus(UkModule ukModule);
//
//    BarterComponent plus(BarterModule barterModule);
//
//    WebComponent plus(WebModule webModule);
//
//    FrancComponent plus(FrancModule francModule);
//
//    SaBankComponent plus(SaBankModule saBankModule);
}

