package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_core.models.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_java_commons.Callback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;

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
