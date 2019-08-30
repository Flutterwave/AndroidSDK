package com.flutterwave.raveandroid.di;

import com.flutterwave.raveandroid.data.ApiService;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class TestNetworkModule {

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
    public NetworkRequestImpl providesNetworkRequestImpl() {
        return Mockito.mock(NetworkRequestImpl.class);
    }

    @Singleton
    @Provides
    public Callbacks.OnGetFeeRequestComplete providesOnGetFeeRequestComplete() {
        return Mockito.mock(Callbacks.OnGetFeeRequestComplete.class);
    }

    @Singleton
    @Provides
    public Callbacks.OnChargeRequestComplete providesCallbacksOnChargeRequestComplete() {
        return Mockito.mock(Callbacks.OnChargeRequestComplete.class);
    }


}
