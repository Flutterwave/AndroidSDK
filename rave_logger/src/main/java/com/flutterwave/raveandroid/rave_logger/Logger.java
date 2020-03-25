package com.flutterwave.raveandroid.rave_logger;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.ExecutorCallback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class Logger {

    String RAVE_LOGGER_TAG = "rave logger tag";
    private LoggerService service;
    private NetworkRequestExecutor executor;

    @Inject
    public Logger(LoggerService service,
                  NetworkRequestExecutor executor) {
        this.service = service;
        this.executor = executor;
    }

    public void logEvent(final RaveEvent event) {
        executor.execute(service.logEvent(event), new ExecutorCallback<String>() {
            @Override
            public void onSuccess(String responseAsJSONString, String responseAsJsonString) {
                Log.d(RAVE_LOGGER_TAG, event.getTitle());
            }

            @Override
            public void onError(ResponseBody responseBody) {
                Log.d(RAVE_LOGGER_TAG, responseBody);
            }

            @Override
            public void onCallFailure(String exceptionMessage) {
                Log.d(RAVE_LOGGER_TAG, exceptionMessage);
            }
        });
    }
}
