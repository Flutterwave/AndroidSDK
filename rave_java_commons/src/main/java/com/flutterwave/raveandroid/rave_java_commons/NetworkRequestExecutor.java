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

    /**
     * Runs a network call, parses the response if successful and passes back to the provided {@link ExecutorCallback}
     *
     * @param call         Network call to be run
     * @param responseType {@link Type} of response Class. (Generic type not used because of type erasure.)
     * @param callback     Callback to receive results (or errors)
     * @param <T>          Expected response type
     */
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
}

