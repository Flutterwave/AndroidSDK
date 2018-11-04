package com.flutterwave.raveandroid.data;

import android.support.annotation.NonNull;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.GhChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.RequeryResponsev2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class NetworkRequestImpl implements DataRequest.NetworkRequest {

    private Retrofit retrofit;
    private ApiService service;
    private String errorParsingError = "An error occurred parsing the error response";

    private ErrorBody parseErrorJson(String errorStr) {

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ErrorBody>() {
            }.getType();
            return gson.fromJson(errorStr, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ErrorBody("error", errorParsingError);
        }

    }

    @Override
    public void chargeCard(ChargeRequestBody body, final Callbacks.OnChargeRequestComplete callback) {

        createService();

        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ChargeResponse>() {}.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });

    }

    @Override
    public void chargeGhanaMobileMoneyWallet(ChargeRequestBody body, final Callbacks.OnGhanaChargeRequestComplete callback) {

        createService();

        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GhChargeResponse>() {}.getType();
                    GhChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });

    }

    @Override
    public void validateChargeCard(ValidateChargeBody body, final Callbacks.OnValidateChargeCardRequestComplete callback) {

        createService();

        Call<String> call = service.validateCardCharge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ChargeResponse>() {}.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });

    }

    @Override
    public void validateAccountCard(ValidateChargeBody body, final Callbacks.OnValidateChargeCardRequestComplete callback) {

        createService();

        Call<String> call = service.validateAccountCharge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ChargeResponse>() {}.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });
    }

    @Override
    public void requeryTxv2(RequeryRequestBodyv2 requeryRequestBody, final Callbacks.OnRequeryRequestv2Complete callback) {

        createService();

        Call<String> call = service.requeryTx_v2(requeryRequestBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonResponse = response.body();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.has("status")) {
                            jsonObject.put("status", "Transaction successfully fetched");
                            jsonResponse = jsonObject.toString();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Type type = new TypeToken<RequeryResponsev2>() {}.getType();
                    RequeryResponsev2 requeryResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(requeryResponse, jsonResponse);
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });
    }

    @Override
    public void requeryTx(RequeryRequestBody requeryRequestBody, final Callbacks.OnRequeryRequestComplete callback) {

        createService();

        Call<String> call = service.requeryTx(requeryRequestBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonResponse = response.body();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.has("status")) {
                            jsonObject.put("status", "Transaction successfully fetched");
                            jsonResponse = jsonObject.toString();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Type type = new TypeToken<RequeryResponse>() {}.getType();
                    RequeryResponse requeryResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(requeryResponse, jsonResponse);
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });
    }

    @Override
    public void getBanks(final Callbacks.OnGetBanksRequestComplete callback) {

        createService();

        Call<List<Bank>> call = service.getBanks();

        call.enqueue(new Callback<List<Bank>>() {
            @Override
            public void onResponse(@NonNull Call<List<Bank>> call, @NonNull Response<List<Bank>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else {
                    try {
                        ErrorBody error = (ErrorBody) retrofit.
                                responseBodyConverter(ErrorBody.class, new Annotation[0])
                                .convert(response.errorBody());
                        callback.onError(error.getMessage());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("An error occurred while retrieving banks");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Bank>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void chargeAccount(ChargeRequestBody body, final Callbacks.OnChargeRequestComplete callback) {

        createService();

        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ChargeResponse>() {}.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);
                        callback.onError(error.getMessage(), errorBody);
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });

    }

    @Override
    public void chargeToken(Payload payload, final Callbacks.OnChargeRequestComplete callback) {

        createService();

        Call<String> call = service.chargeToken(payload);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ChargeResponse>() {}.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);

                        if (error.getMessage().equalsIgnoreCase("ERR") &&
                                error.getData() != null &&
                                error.getData().getCode().contains("expired")) {
                            callback.onError("expired", errorBody);
                        }
                        else {
                            callback.onError(error.getMessage(), errorBody);
                        }
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                        callback.onError("error", errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage(), "");
            }
        });

    }

    @Override
    public void getFee(FeeCheckRequestBody body, final Callbacks.OnGetFeeRequestComplete callback) {

        createService();

        Call<FeeCheckResponse> call = service.checkFee(body);

        call.enqueue(new Callback<FeeCheckResponse>() {
            @Override
            public void onResponse(Call<FeeCheckResponse> call, Response<FeeCheckResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else {
                    try {
                        ErrorBody error = (ErrorBody) retrofit.
                                responseBodyConverter(ErrorBody.class, new Annotation[0])
                                .convert(response.errorBody());
                        callback.onError(error.getMessage());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("An error occurred while retrieving transaction charge");
                    }
                }
            }

            @Override
            public void onFailure(Call<FeeCheckResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private void createService() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient.addNetworkInterceptor(logging).connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(RavePayActivity.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        service = retrofit.create(ApiService.class);
    }

}
