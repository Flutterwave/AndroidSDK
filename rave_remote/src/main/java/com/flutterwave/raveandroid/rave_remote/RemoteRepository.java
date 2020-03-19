package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_core.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_core.ChargeResponse;
import com.flutterwave.raveandroid.rave_java_commons.Callback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Retrofit;

public class RemoteRepository {

    private RaveService service;
    private NetworkRequestExecutor executor;

    @Inject
    public RemoteRepository(RaveService service,
                              NetworkRequestExecutor executor) {
        this.service = service;
        this.executor = executor;
    }

    public void charge(ChargeRequestBody body, final ResultCallback callback) {
        executor.execute(service.charge(body), new Callback() {
            @Override
            public void onSuccess(String responseAsJSONString) {
                callback.onResult(true, responseAsJSONString);
            }

            @Override
            public void onError(String responseAsJSONString) {
                callback.onResult(false, responseAsJSONString);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                callback.onResult(false, exceptionMessage);
            }
        });
    }

}
