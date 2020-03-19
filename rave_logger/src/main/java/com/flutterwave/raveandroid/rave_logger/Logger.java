package com.flutterwave.raveandroid.rave_logger;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Callback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;

import javax.inject.Inject;

public class Logger {

    String RAVE_LOGGER_TAG = "rave logger tag";
    private LoggerService service;
    private NetworkRequestExecutor executor;

    @Inject
    public Logger(LoggerService service, NetworkRequestExecutor executor) {
        this.service = service;
        this.executor = executor;
    }

    public void logEvent(final RaveEvent event) {
        executor.execute(service.logEvent(event), new Callback() {
            @Override
            public void onSuccess(String responseAsJSONString) {
                Log.d(RAVE_LOGGER_TAG, event.getTitle());
            }

            @Override
            public void onError(String responseAsJSONString) {
                Log.d(RAVE_LOGGER_TAG, responseAsJSONString);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.d(RAVE_LOGGER_TAG, exceptionMessage);
            }
        });
    }
}
