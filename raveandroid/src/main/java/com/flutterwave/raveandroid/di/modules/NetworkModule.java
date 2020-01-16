package com.flutterwave.raveandroid.di.modules;

import com.babylon.certificatetransparency.BasicAndroidCTLogger;
import com.babylon.certificatetransparency.CTInterceptorBuilder;
import com.flutterwave.raveandroid.data.ApiService;
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
public class NetworkModule {

    @Inject
    String baseUrl;

    private Retrofit retrofit;
    private ApiService apiService;

    public NetworkModule() {
    }

    @Inject
    public NetworkModule(String url) {
        baseUrl = url;
    }

    @Singleton
    @Provides
    @Named("mainRetrofit")
    public Retrofit providesRetrofit() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        CTInterceptorBuilder ctInterceptorBuilder = new CTInterceptorBuilder();
        BasicAndroidCTLogger basicAndroidCTLogger = new BasicAndroidCTLogger(true);
        ctInterceptorBuilder.includeHost("ravesandboxapi.flutterwave.com")
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
