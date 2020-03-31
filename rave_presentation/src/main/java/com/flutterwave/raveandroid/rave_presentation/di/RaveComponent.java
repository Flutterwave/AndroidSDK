package com.flutterwave.raveandroid.rave_presentation.di;


import android.content.Context;

import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_cache.di.CacheModule;
import com.flutterwave.raveandroid.rave_core.di.DeviceIdGetterModule;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RemoteModule.class, EventLoggerModule.class, DeviceIdGetterModule.class})
public interface RaveComponent {

    RemoteRepository networkImpl();

    EventLogger eventLogger();
//
//    SharedPrefsRepo sharedManager();
//
//    Context context();

    DeviceIdGetter deviceIdGetter();

    UrlValidator urlValidator();

    CardNoValidator cardNoValidator();

    PayloadToJsonConverter payloadToJsonConverter();

    TransactionStatusChecker transactionStatusChecker();

    PayloadEncryptor payloadEncryptor();

    PayloadToJson payloadToJson();

    Gson gson();

    CardComponent plus(CardModule cardModule);
}

