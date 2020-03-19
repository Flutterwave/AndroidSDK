package com.flutterwave.raveandroid.rave_java_commons;

public interface Callback {
    void onSuccess(String responseAsJSONString);
    void onError(String responseAsJSONString);
    void onFailure(String exceptionMessage);
}