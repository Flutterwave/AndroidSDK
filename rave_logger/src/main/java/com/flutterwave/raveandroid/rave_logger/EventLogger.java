package com.flutterwave.raveandroid.rave_logger;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.ExecutorCallback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class EventLogger {

    String RAVE_LOGGER_TAG = "rave logger tag";
    private LoggerService service;
    private NetworkRequestExecutor executor;

    @Inject
    public EventLogger(LoggerService service,
                       NetworkRequestExecutor executor) {
        this.service = service;
        this.executor = executor;
    }

    public void logEvent(final Event event) {
        executor.execute(service.logEvent(event),
                new TypeToken<String>() {
                }.getType(),
                new ExecutorCallback<String>() {
            @Override
            public void onSuccess(String responseAsJSONString, String responseAsJsonString) {
                Log.d(RAVE_LOGGER_TAG, event.getTitle());
            }

            @Override
            public void onError(ResponseBody responseBody) {
                try {
                    Log.d(RAVE_LOGGER_TAG, responseBody.string()
                    );
                } catch (IOException e) {
                    Log.d(RAVE_LOGGER_TAG, "Event log action unsuccessful");
                }
            }

                    @Override
                    public void onParseError(String message, String responseAsJsonString) {
                        Log.d(RAVE_LOGGER_TAG, message);
                    }

            @Override
            public void onCallFailure(String exceptionMessage) {
                Log.d(RAVE_LOGGER_TAG, exceptionMessage);
            }
        });
    }
}
