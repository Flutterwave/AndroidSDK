package com.flutterwave.raveandroid.data;

import android.support.annotation.NonNull;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.MobileMoneyChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.RequeryResponsev2;
import com.flutterwave.raveandroid.responses.SaveCardResponse;
import com.flutterwave.raveandroid.responses.SendRaveOtpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hamzafetuga on 18/07/2017.
 */
@Singleton
public class NetworkRequestImpl implements DataRequest.NetworkRequest {

    Retrofit mainRetrofit;
    Retrofit eventLoggingRetrofit;
    ApiService service;
    EventLoggerService eventLoggerService;
    Gson gson;
    private String errorParsingError = "An error occurred parsing the error response";

    @Inject
    public NetworkRequestImpl(@Named("mainRetrofit") Retrofit mainRetrofit,
                              @Named("eventLoggingRetrofit") Retrofit eventLoggingRetrofit,
                              ApiService service,
                              EventLoggerService eventLoggerService,
                              Gson gson) {
        this.mainRetrofit = mainRetrofit;
        this.eventLoggingRetrofit = eventLoggingRetrofit;
        this.service = service;
        this.eventLoggerService = eventLoggerService;
        this.gson = gson;
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

    @Override
    public void charge(ChargeRequestBody body, final Callbacks.OnChargeRequestComplete callback) {


        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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
    public void chargeWithPolling(ChargeRequestBody body, final Callbacks.OnChargeRequestComplete callback) {

        Call<String> call = service.chargeWithPolling(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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
    public void chargeMobileMoneyWallet(ChargeRequestBody body, final Callbacks.OnGhanaChargeRequestComplete callback) {


        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Type type = new TypeToken<MobileMoneyChargeResponse>() {
                    }.getType();
                    MobileMoneyChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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


        Call<String> call = service.validateCardCharge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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


        Call<String> call = service.validateAccountCharge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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
                    Type type = new TypeToken<RequeryResponsev2>() {
                    }.getType();
                    RequeryResponsev2 requeryResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(requeryResponse, jsonResponse);
                } else {
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
    public void requeryPayWithBankTx(RequeryRequestBody requeryRequestBody, final Callbacks.OnRequeryRequestComplete callback) {


        Call<String> call = service.requeryPayWithBankTx(requeryRequestBody);

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
                    Type type = new TypeToken<RequeryResponse>() {
                    }.getType();
                    RequeryResponse requeryResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(requeryResponse, jsonResponse);
                } else {
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
    public void saveCardToRave(SaveCardRequestBody saveCardRequestBody, final Callbacks.OnSaveCardRequestComplete callback) {

        Call<String> call = service.saveCardToRave(saveCardRequestBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonResponse = response.body();
                if (response.isSuccessful()) {
                    Type type = new TypeToken<SaveCardResponse>() {
                    }.getType();
                    SaveCardResponse saveCardResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(saveCardResponse, jsonResponse);
                } else {
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
    public void lookupSavedCards(LookupSavedCardsRequestBody requestBody,
                                 final Callbacks.OnLookupSavedCardsRequestComplete callback) {

        Call<String> call = service.lookupSavedCards(requestBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonResponse = response.body();
                if (response.isSuccessful()) {
                    Type type = new TypeToken<LookupSavedCardsResponse>() {
                    }.getType();
                    LookupSavedCardsResponse lookupSavedCardsResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(lookupSavedCardsResponse, jsonResponse);
                } else {
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
    public void sendRaveOtp(SendOtpRequestBody requestBody,
                            final Callbacks.OnSendRaveOTPRequestComplete callback) {

        Call<String> call = service.sendRaveOtp(requestBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonResponse = response.body();
                if (response.isSuccessful()) {
                    Type sendOtpType = new TypeToken<SendRaveOtpResponse>() {
                    }.getType();
                    SendRaveOtpResponse sendRaveOtpResponse = gson.fromJson(jsonResponse, sendOtpType);
                    callback.onSuccess(sendRaveOtpResponse, jsonResponse);
                } else {
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
                    Type type = new TypeToken<RequeryResponse>() {
                    }.getType();
                    RequeryResponse requeryResponse = gson.fromJson(jsonResponse, type);
                    callback.onSuccess(requeryResponse, jsonResponse);
                } else {
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


        Call<List<Bank>> call = service.getBanks();

        call.enqueue(new Callback<List<Bank>>() {
            @Override
            public void onResponse(@NonNull Call<List<Bank>> call, @NonNull Response<List<Bank>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        ErrorBody error = (ErrorBody) mainRetrofit.
                                responseBodyConverter(ErrorBody.class, new Annotation[0])
                                .convert(response.errorBody());
                        callback.onError(error.getMessage());
                    } catch (Exception e) {
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


        Call<String> call = service.charge(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
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


        Call<String> call = service.chargeToken(payload);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {

                    Type type = new TypeToken<ChargeResponse>() {
                    }.getType();
                    ChargeResponse chargeResponse = gson.fromJson(response.body(), type);
                    callback.onSuccess(chargeResponse, response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorBody error = parseErrorJson(errorBody);

                        if (error.getMessage().equalsIgnoreCase("ERR") &&
                                error.getData() != null &&
                                error.getData().getCode().contains("expired")) {
                            callback.onError("expired", errorBody);
                        } else {
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


        Call<FeeCheckResponse> call = service.checkFee(body);

        call.enqueue(new Callback<FeeCheckResponse>() {
            @Override
            public void onResponse(Call<FeeCheckResponse> call, Response<FeeCheckResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        ErrorBody error = (ErrorBody) mainRetrofit.
                                responseBodyConverter(ErrorBody.class, new Annotation[0])
                                .convert(response.errorBody());
                        callback.onError(error.getMessage());
                    } catch (Exception e) {
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

    @Override
    public void logEvent(EventBody body, final Callbacks.OnLogEventComplete callback) {


        Call<String> call = eventLoggerService.logEvent(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        ErrorBody error = (ErrorBody) eventLoggingRetrofit.
                                responseBodyConverter(ErrorBody.class, new Annotation[0])
                                .convert(response.errorBody());
                        callback.onError(error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError(errorParsingError);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

}
