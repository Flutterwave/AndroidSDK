package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.flutterwave.raveandroid.rave_logger.LoggerService;
import com.flutterwave.raveandroid.rave_remote.ApiService;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.google.gson.Gson;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class TestNetworkModule {

    @Singleton
    @Provides
    public Gson provideGson() {
        return Mockito.mock(Gson.class);
    }

    @Provides
    public Retrofit providesRetrofit() {
        return Mockito.mock(Retrofit.class);
    }

    @Singleton
    @Provides
    public ApiService providesApiService() {
        return Mockito.mock(ApiService.class);
    }

    @Singleton
    @Provides
    public LoggerService providesLoggerService() {
        return Mockito.mock(LoggerService.class);
    }

    @Singleton
    @Provides
    public NetworkRequestExecutor providesNetworkExecutor() {
        return Mockito.mock(NetworkRequestExecutor.class);
    }

    @Singleton
    @Provides
    public RemoteRepository providesNetworkRequestImpl() {
        return Mockito.mock(RemoteRepository.class);
    }

}
