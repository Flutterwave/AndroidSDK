package com.flutterwave.raveandroid.rave_remote.di;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_remote.ApiService;
import com.google.gson.Gson;

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

@Module
public class RemoteModule {

    @Inject
    String baseUrl;

    private Retrofit retrofit;
    private Retrofit barterRetrofit;
    private ApiService apiService;
    private ApiService barterApiService;

    public RemoteModule() {
    }

    @Inject
    public RemoteModule(String url) {
        baseUrl = url;
    }

    @Provides
    @Named("mainRetrofit")
    public Retrofit providesRetrofit() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient.addNetworkInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
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

    @Provides
    @Named("barterRetrofit")
    public Retrofit providesBarterRetrofit() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient.addNetworkInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        barterRetrofit = new Retrofit.Builder()
                .baseUrl(RaveConstants.CARD_CHECK_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return barterRetrofit;
    }

    @Singleton
    @Provides
    public Gson gson() {
        return new Gson();
    }

    @Provides
    @Named("mainApiService")
    public ApiService providesApiService() {
        apiService = retrofit.create(ApiService.class);
        return apiService;
    }

    @Provides
    @Named("barterApiService")
    public ApiService providesBarterApiService() {
        barterApiService = barterRetrofit.create(ApiService.class);
        return barterApiService;
    }

}
