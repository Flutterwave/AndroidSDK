package com.flutterwave.raveandroid.rave_java_commons;

import okhttp3.ResponseBody;

public interface GenericExecutorCallback {
    void onSuccess(String response);

    void onError(ResponseBody responseBody);

    void onCallFailure(String exceptionMessage);

    void onParseError(String error);
}
