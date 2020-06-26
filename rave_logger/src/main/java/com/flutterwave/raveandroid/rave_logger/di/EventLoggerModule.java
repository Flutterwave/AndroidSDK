package com.flutterwave.raveandroid.rave_logger.di;

import com.flutterwave.raveandroid.rave_logger.LoggerService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class EventLoggerModule {

    // move to commons module
    private String EVENT_LOGGING_URL = "https://kgelfdz7mf.execute-api.us-east-1.amazonaws.com/";

    private Retrofit providesRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient
//                .addNetworkInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(EVENT_LOGGING_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    public LoggerService providesLoggerService() {
        return providesRetrofit().create(LoggerService.class);
    }
}
