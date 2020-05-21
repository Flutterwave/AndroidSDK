package com.flutterwave.raveandroid.rave_remote.di;

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
    private ApiService apiService;

    public RemoteModule() {
    }

    @Inject
    public RemoteModule(String url) {
        baseUrl = url;
    }

    @Singleton
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

    @Singleton
    @Provides
    public Gson gson() {
        return new Gson();
    }

    @Singleton
    @Provides
    public ApiService providesApiService() {
        apiService = retrofit.create(ApiService.class);
        return apiService;
    }

}
