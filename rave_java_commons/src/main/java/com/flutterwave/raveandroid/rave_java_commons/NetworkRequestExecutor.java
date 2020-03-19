package com.flutterwave.raveandroid.rave_java_commons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkRequestExecutor {

    @Inject
    public NetworkRequestExecutor() {}

    public void execute(Call<String> call,
                        final Callback callback) {

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}

