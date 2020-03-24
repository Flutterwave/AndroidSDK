package com.flutterwave.raveandroid.rave_remote;

public interface ResultCallback<T> {
    //    void onResult(boolean status, T response);
    void onSuccess(T response);

    void onError(String message);
}
