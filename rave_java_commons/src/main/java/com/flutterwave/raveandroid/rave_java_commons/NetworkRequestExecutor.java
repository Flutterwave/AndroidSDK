package com.flutterwave.raveandroid.rave_java_commons;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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
                            final Callback<T> callback) {

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        T parsedResponse = gson.fromJson(response.body(), responseType);
                        callback.onSuccess(parsedResponse);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        callback.onError(RaveConstants.responseParsingError);
                    }
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError(RaveConstants.errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    Type getType(final Class<?> rawClass, final Class<?> parameter) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{parameter};
            }

            @Override
            public Type getRawType() {
                return rawClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}

