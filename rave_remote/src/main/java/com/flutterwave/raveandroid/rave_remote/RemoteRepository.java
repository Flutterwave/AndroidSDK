package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_java_commons.Callback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

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
        executor.execute(service.charge(body), new TypeToken<String>() {
                }.getType(),
                new Callback<String>() {
                    @Override
                    public void onSuccess(String responseAsJSONString) {
                        callback.onSuccess(responseAsJSONString);
                    }

                    @Override
                    public void onError(String responseAsJSONString) {
                        callback.onError(responseAsJSONString);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        callback.onError(exceptionMessage);
                    }
                });
    }

}
