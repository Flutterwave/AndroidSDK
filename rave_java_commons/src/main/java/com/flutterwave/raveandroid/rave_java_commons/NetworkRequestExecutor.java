package com.flutterwave.raveandroid.rave_java_commons;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkRequestExecutor {

    Gson gson;

    @Inject
    public NetworkRequestExecutor(Gson gson) {
        this.gson = gson;
    }

    public <T> void execute(Call<String> call,
                            final Type responseType,
                            final ExecutorCallback<T> callback) {

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        T parsedResponse = gson.fromJson(response.body(), responseType);
                        callback.onSuccess(parsedResponse, response.body());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        callback.onParseError(RaveConstants.responseParsingError, response.body());
                    }
                } else {
                    callback.onError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onCallFailure(t.getMessage());
            }
        });
    }

    public <T> void executeSpecificCall(Call<T> call,
                                        final ExecutorCallback<T> callback) {

        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body(), response.body().toString());
                } else {
                    callback.onError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onCallFailure(t.getMessage());
            }
        });
    }


    public void execute(Call<String> call,
                        final GenericExecutorCallback callback) {
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        callback.onParseError(RaveConstants.responseParsingError);
                    }
                } else {
                    callback.onError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onCallFailure(t.getMessage());
            }
        });
    }
}

