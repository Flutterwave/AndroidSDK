package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_core.models.ErrorBody;
import com.flutterwave.raveandroid.rave_java_commons.GenericExecutorCallback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okhttp3.ResponseBody;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.errorParsingError;

public class RemoteRepository {

    private final Gson gson;
    private RaveService service;
    private NetworkRequestExecutor executor;

    @Inject
    public RemoteRepository(RaveService service,
                            Gson gson,
                            NetworkRequestExecutor executor) {
        this.service = service;
        this.gson = gson;
        this.executor = executor;
    }

    public void charge(ChargeRequestBody body, final ResultCallback callback) {
        executor.execute(
                service.charge(body),
                new GenericExecutorCallback() {
                    @Override
                    public void onSuccess(String responseAsJSONString) {
                        callback.onSuccess(responseAsJSONString);
                    }

                    @Override
                    public void onError(ResponseBody responseBody) {
                        try {
                            String errorBody = responseBody.string();
                            ErrorBody error = parseErrorJson(errorBody);
                            callback.onError(error.getMessage());
                        } catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                            onParseError(errorParsingError);
                        }
                    }

                    @Override
                    public void onCallFailure(String exceptionMessage) {
                        callback.onError(exceptionMessage);
                    }

                    @Override
                    public void onParseError(String error) {
                        callback.onError(error);
                    }
                });
    }

    private ErrorBody parseErrorJson(String errorStr) {

        try {
            Type type = new TypeToken<ErrorBody>() {
            }.getType();
            return gson.fromJson(errorStr, type);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorBody("error", errorParsingError);
        }

    }
}
