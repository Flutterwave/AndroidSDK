package com.flutterwave.raveandroid.di.modules;

import com.babylon.certificatetransparency.BasicAndroidCTLogger;
import com.babylon.certificatetransparency.CTInterceptorBuilder;
import com.flutterwave.raveandroid.data.EventLoggerService;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.flutterwave.raveandroid.RaveConstants.EVENT_LOGGING_URL;


@Module
public class EventLoggerModule {

    String baseUrl = EVENT_LOGGING_URL;

    private Retrofit retrofit;
    private EventLoggerService eventLoggerService;

    @Inject
    public EventLoggerModule() {
    }

    @Singleton
    @Provides
    @Named("eventLoggingRetrofit")
    public Retrofit providesRetrofit() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        CTInterceptorBuilder ctInterceptorBuilder = new CTInterceptorBuilder();
        BasicAndroidCTLogger basicAndroidCTLogger = new BasicAndroidCTLogger(true);
        ctInterceptorBuilder.includeHost("ravesandboxapi.flutterwave.com")
                .includeHost("ravesandboxapi.flutterwave.com")
                .includeHost("rave-webhook.herokuapp.com/receivepayment")
                .includeHost("kgelfdz7mf.execute-api.us-east-1.amazonaws.com/")
                .includeHost("api.ravepay.co");
        ctInterceptorBuilder.setLogger(basicAndroidCTLogger);

        OkHttpClient okHttpClient = httpClient.addNetworkInterceptor(ctInterceptorBuilder.build()).addNetworkInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    @Singleton
    @Provides
    public EventLoggerService providesEventLoggerService() {
        eventLoggerService = retrofit.create(EventLoggerService.class);
        return eventLoggerService;
    }

}
