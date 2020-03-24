package com.flutterwave.raveandroid.rave_remote;


import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBodyv2;
import com.flutterwave.raveandroid.rave_remote.requests.SaveCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SendOtpRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public interface ApiService {

    @POST("/flwv3-pug/getpaidx/api/charge")
//    Call<ChargeResponse> charge(@Body ChargeRequestBody body);
    Call<String> charge(@Body ChargeRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/charge?use_polling=1")
//    Call<ChargeResponse> charge(@Body ChargeRequestBody body);
    Call<String> chargeWithPolling(@Body ChargeRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/validatecharge")
    Call<String> validateCardCharge(@Body ValidateChargeBody body);
//    Call<ChargeResponse> validateCardCharge(@Body ValidateChargeBody body);

    @POST("/flwv3-pug/getpaidx/api/validate")
    Call<String> validateAccountCharge(@Body ValidateChargeBody body);
//    Call<ChargeResponse> validateAccountCharge(@Body ValidateChargeBody body);

    @POST("/flwv3-pug/getpaidx/api/verify/mpesa")
    Call<String> requeryTx(@Body RequeryRequestBody body);
//    Call<RequeryResponse> requeryTx(@Body RequeryRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/verify/pwbt")
    Call<String> requeryPayWithBankTx(@Body RequeryRequestBody body);
//    Call<RequeryResponse> requeryTx(@Body RequeryRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/v2/verify")
    Call<String> requeryTx_v2(@Body RequeryRequestBodyv2 body);

    @GET("/flwv3-pug/getpaidx/api/flwpbf-banks.js?json=1")
    Call<List<Bank>> getBanks();

    @POST("/flwv3-pug/getpaidx/api/tokenized/charge")
    Call<String> chargeToken(@Body Payload payload);

    @POST("/flwv3-pug/getpaidx/api/fee")
    Call<FeeCheckResponse> checkFee(@Body FeeCheckRequestBody body);

    @POST("/v2/gpx/devices/save")
    Call<String> saveCardToRave(@Body SaveCardRequestBody body);

    @POST("/v2/gpx/users/lookup")
    Call<String> lookupSavedCards(@Body LookupSavedCardsRequestBody requestBody);

    @POST("/v2/gpx/users/send_otp")
    Call<String> sendRaveOtp(@Body SendOtpRequestBody requestBody);
}
