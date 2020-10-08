package com.flutterwave.raveandroid.rave_remote;


import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_core.models.ErrorBody;
import com.flutterwave.raveandroid.rave_java_commons.ExecutorCallback;
import com.flutterwave.raveandroid.rave_java_commons.NetworkRequestExecutor;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RemoveSavedCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SaveCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SendOtpRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaBankAccountResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SendRaveOtpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.CHARGE_TYPE_SA_BANK;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.expired;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.tokenExpired;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.tokenNotFound;

/**
 * Created by hamzafetuga on 18/07/2017.
 */
@Singleton
public class RemoteRepository {

    private Retrofit mainRetrofit;
    private ApiService service;
    private Gson gson;
    private NetworkRequestExecutor executor;
    private String errorParsingError = "An error occurred parsing the error response";

    @Inject
    public RemoteRepository(@Named("mainRetrofit") Retrofit mainRetrofit,
                            ApiService service,
                            Gson gson,
                            NetworkRequestExecutor executor) {
        this.mainRetrofit = mainRetrofit;
        this.service = service;
        this.gson = gson;
        this.executor = executor;
    }

    public void charge(String publicKey, String chargeType, ChargeRequestBody body, final ResultCallback callback) {
        executor.execute(
                service.encryptedCharge(chargeType, "Bearer " + publicKey, body),
                new TypeToken<ChargeResponse>() {
                }.getType(),
                new GenericNetworkCallback<ChargeResponse>(callback)
        );
    }

    public void charge(String publicKey, String chargeType, Payload body, final ResultCallback callback) {
        executor.execute(
                service.charge(chargeType, "Bearer " + publicKey, body),
                new TypeToken<ChargeResponse>() {
                }.getType(),
                new GenericNetworkCallback<ChargeResponse>(callback)
        );
    }

    /**
     * @deprecated This has been deprecated in favor of the {@link RemoteRepository#charge(String, String, ChargeRequestBody, ResultCallback)} v3 charge}.
     * It's only left for use for saved card charge and barter charges which have not yet been migrated to v3.
     * Other charge types might not work well with this route.
     */
    public void chargeV2(ChargeRequestBody body, final ResultCallback callback) {
        executor.execute(
                service.chargeV2(body),
                new TypeToken<ChargeResponse>() {
                }.getType(),
                new GenericNetworkCallback<ChargeResponse>(callback)
        );
    }


    public void chargeSaBankAccount(String publicKey, Payload payload, final ResultCallback callback) {
        Call<String> call = service.charge(CHARGE_TYPE_SA_BANK, "Bearer " + publicKey, payload);

        executor.execute(
                call,
                new TypeToken<SaBankAccountResponse>() {
                }.getType(),
                new GenericNetworkCallback<SaBankAccountResponse>(callback)
        );
    }


    public void validateCharge(String publicKey, ValidateChargeBody body, final ResultCallback callback) {

        Call<String> call = service.validateCharge("Bearer " + publicKey, body);

        executor.execute(
                call,
                new TypeToken<ChargeResponse>() {
                }.getType(),
                new GenericNetworkCallback<ChargeResponse>(callback)
        );

    }

    public void requeryTx(String publicKey, RequeryRequestBody requeryRequestBody, final Callbacks.OnRequeryRequestComplete callback) {

        Call<String> call = service.requeryTx("Bearer " + publicKey, requeryRequestBody);

        executor.execute(
                call,
                new TypeToken<RequeryResponse>() {
                }.getType(),
                new RequeryNetworkCallback(callback)
        );
    }

    public void requeryPayWithBankTx(String publicKey, RequeryRequestBody requeryRequestBody, final Callbacks.OnRequeryRequestComplete callback) {

        Call<String> call = service.requeryPayWithBankTx("Bearer " + publicKey, requeryRequestBody);

        executor.execute(
                call,
                new TypeToken<RequeryResponse>() {
                }.getType(),
                new RequeryNetworkCallback(callback)
        );
    }


    public void saveCardToRave(SaveCardRequestBody saveCardRequestBody, final ResultCallback callback) {

        Call<String> call = service.saveCardToRave(saveCardRequestBody);

        executor.execute(
                call,
                new TypeToken<SaveCardResponse>() {
                }.getType(),
                new GenericNetworkCallback<SaveCardResponse>(callback)
        );
    }


    public void lookupSavedCards(LookupSavedCardsRequestBody requestBody,
                                 final ResultCallback callback) {

        Call<String> call = service.lookupSavedCards(requestBody);

        executor.execute(
                call,
                new TypeToken<LookupSavedCardsResponse>() {
                }.getType(),
                new GenericNetworkCallback<LookupSavedCardsResponse>(callback)
        );
    }


    public void deleteASavedCard(RemoveSavedCardRequestBody requestBody,
                                 final ResultCallback callback) {

        Call<String> call = service.deleteSavedCard(requestBody);

        executor.execute(
                call,
                new TypeToken<SaveCardResponse>() {
                }.getType(),
                new GenericNetworkCallback<SaveCardResponse>(callback)
        );
    }


    public void sendRaveOtp(SendOtpRequestBody requestBody,
                            final ResultCallback callback) {

        Call<String> call = service.sendRaveOtp(requestBody);

        executor.execute(
                call,
                new TypeToken<SendRaveOtpResponse>() {
                }.getType(),
                new GenericNetworkCallback<SendRaveOtpResponse>(callback)
        );
    }

    public void getBanks(final ResultCallback callback) {

        Call<String> call = service.getBanks();

        executor.execute(
                call,
                new TypeToken<List<Bank>>() {
                }.getType(),
                new ExecutorCallback<List<Bank>>() {
                    @Override
                    public void onSuccess(List<Bank> response, String responseAsJsonString) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ResponseBody responseBody) {
                        try {
                            ErrorBody error = (ErrorBody) mainRetrofit.
                                    responseBodyConverter(ErrorBody.class, new Annotation[0])
                                    .convert(responseBody);
                            callback.onError(error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onError("An error occurred while retrieving banks");
                        }
                    }

                    @Override
                    public void onParseError(String message, String responseAsJsonString) {
                        callback.onError(message);
                    }

                    @Override
                    public void onCallFailure(String exceptionMessage) {
                        callback.onError(exceptionMessage);
                    }
                }
        );
    }

    public void getFee(FeeCheckRequestBody body, final ResultCallback callback) {


        Call<String> call = service.checkFee(body);

        executor.execute(
                call,
                new TypeToken<FeeCheckResponse>() {
                }.getType(),
                new GenericNetworkCallback<FeeCheckResponse>(callback)
        );
    }


    public void chargeToken(Payload payload, final ResultCallback callback) {

        Call<String> call = service.chargeToken(payload);

        executor.execute(
                call,
                new TypeToken<ChargeResponse>() {
                }.getType(),
                new ExecutorCallback<ChargeResponse>() {
                    @Override
                    public void onSuccess(ChargeResponse response, String responseAsJsonString) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(ResponseBody responseBody) {
                        try {
                            String errorBody = responseBody.string();
                            ErrorBody error = parseErrorJson(errorBody);

                            if (errorBody.contains(tokenNotFound)) {
                                callback.onError(tokenNotFound);
                            } else if (errorBody.contains(expired)) {
                                callback.onError(tokenExpired);
                            } else {
                                callback.onError(error.getMessage());
                            }
                        } catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                            callback.onError(errorParsingError);
                        }
                    }

                    @Override
                    public void onParseError(String message, String responseAsJsonString) {
                        callback.onError(message);
                    }

                    @Override
                    public void onCallFailure(String exceptionMessage) {
                        callback.onError(exceptionMessage);
                    }
                }
        );
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

    /**
     * A generic class for handling error parsing and feeding back to the (presenter) callbacks.
     * Other network calls tha want to perform other operations in their errors implement their own callbacks
     * extending {@link ExecutorCallback}
     *
     * @param <T> The response type expected.
     */
    private class GenericNetworkCallback<T> implements ExecutorCallback<T> {
        private final ResultCallback resultCallback;

        GenericNetworkCallback(ResultCallback callback) {
            this.resultCallback = callback;
        }

        @Override
        public void onSuccess(T response, String responseAsJsonString) {
            resultCallback.onSuccess(response);
        }

        @Override
        public void onError(ResponseBody responseBody) {
            try {
                String errorBody = responseBody.string();
                ErrorBody error = parseErrorJson(errorBody);
                resultCallback.onError(error.getMessage());
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                try {
                    onParseError(errorParsingError, responseBody.string());
                } catch (IOException ex) {
                    onParseError(errorParsingError, "");
                }
            }
        }

        @Override
        public void onCallFailure(String exceptionMessage) {
            resultCallback.onError(exceptionMessage);
        }

        @Override
        public void onParseError(String message, String responseAsJsonString) {
            resultCallback.onError(message);
        }
    }

    private class RequeryNetworkCallback implements ExecutorCallback<RequeryResponse> {
        private final Callbacks.OnRequeryRequestComplete callback;

        RequeryNetworkCallback(Callbacks.OnRequeryRequestComplete callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(RequeryResponse response, String responseAsJsonString) {
            if (response.getStatus() != null) {
                response.setStatus("Transaction successfully fetched");
            }
            callback.onSuccess(response, responseAsJsonString);
        }

        @Override
        public void onError(ResponseBody responseBody) {
            try {
                String errorBody = responseBody.string();
                ErrorBody error = parseErrorJson(errorBody);
                callback.onError(error.getMessage(), errorBody);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                callback.onError(errorParsingError, errorParsingError);
            }
        }

        @Override
        public void onParseError(String message, String responseAsJsonString) {
            callback.onError(message, message);
        }

        @Override
        public void onCallFailure(String exceptionMessage) {
            callback.onError(exceptionMessage, exceptionMessage);
        }
    }
}
