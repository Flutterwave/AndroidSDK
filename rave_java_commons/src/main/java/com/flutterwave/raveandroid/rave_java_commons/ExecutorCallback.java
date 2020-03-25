package com.flutterwave.raveandroid.rave_java_commons;

import okhttp3.ResponseBody;

public interface ExecutorCallback<T> {
    void onSuccess(T response, String responseAsJsonString);

    void onError(ResponseBody responseBody);

    void onParseError(String message, String responseAsJsonString);

    void onCallFailure(String exceptionMessage);
}