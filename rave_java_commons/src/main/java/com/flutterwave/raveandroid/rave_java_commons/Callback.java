package com.flutterwave.raveandroid.rave_java_commons;

public interface Callback<T> {
    void onSuccess(T response);
    void onError(String responseAsJSONString);
    void onFailure(String exceptionMessage);
}