package com.flutterwave.raveandroid.rave_remote.di;

import com.flutterwave.raveandroid.rave_remote.RaveService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class RemoteModule {

    // move to commons module?
    private static String STAGING_URL = "https://ravesandboxapi.flutterwave.com";
    private static String LIVE_URL = "https://api.ravepay.co";

    @Singleton
    @Provides
    public Retrofit providesRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient.addNetworkInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(STAGING_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    public RaveService providesRaveService(Retrofit retrofit) {
        return retrofit.create(RaveService.class);
    }
}
