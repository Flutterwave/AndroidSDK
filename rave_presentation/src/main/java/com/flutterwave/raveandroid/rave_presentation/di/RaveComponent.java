package com.flutterwave.raveandroid.rave_presentation.di;


import android.content.Context;

import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_cache.di.CacheModule;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_presentation.data.DeviceIdGetter;
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
@Component(modules = {AndroidModule.class, RemoteModule.class, EventLoggerModule.class, CacheModule.class})
public interface RaveComponent {

    RemoteRepository networkImpl();

    EventLogger eventLogger();

    Context getContext();

    DeviceIdGetter deviceIdGetter();

    UrlValidator urlValidator();

    CardNoValidator cardNoValidator();

    PayloadToJsonConverter payloadToJsonConverter();

    TransactionStatusChecker transactionStatusChecker();

    PayloadEncryptor payloadEncryptor();

    PayloadToJson payloadToJson();

    SharedPrefsRepo sharedManager();

    Gson gson();

}

