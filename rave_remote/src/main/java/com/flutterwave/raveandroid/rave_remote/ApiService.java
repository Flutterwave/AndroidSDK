package com.flutterwave.raveandroid.rave_remote;


import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RemoveSavedCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBodyv2;
import com.flutterwave.raveandroid.rave_remote.requests.SaveCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SendOtpRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public interface ApiService {

    @POST("v3/sdkcheckout/charges")
//    @POST("/flwv3-pug/getpaidx/api/charge")
    Call<String> charge(@Query("type") String chargeType, @Header("Authorization") String authorizationHeader, @Body ChargeRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/charge?use_polling=1")
//    Call<ChargeResponse> charge(@Body ChargeRequestBody body);
    Call<String> chargeWithPolling(@Body ChargeRequestBody body);
    // Todo: Handle v3 charge with polling and other v3 methods

    @POST("/v3/sdkcheckout/validate-charge")
//    @POST("/flwv3-pug/getpaidx/api/validatecharge")
    Call<String> validateCharge(@Header("Authorization") String authorizationHeader, @Body ValidateChargeBody body);
//    Call<ChargeResponse> validateCardCharge(@Body ValidateChargeBody body);
    // Todo: code optimization, remove unused methods and fields.

    @POST("v3/sdkcheckout/mpesa-verify")
    Call<String> requeryTx(@Header("Authorization") String authorizationHeader, @Body RequeryRequestBody body);
//    Call<RequeryResponse> requeryTx(@Body RequeryRequestBody body);

    @POST("/v3/sdkcheckout/pwbt-verify")
    Call<String> requeryPayWithBankTx(@Header("Authorization") String authorizationHeader, @Body RequeryRequestBody body);
//    Call<RequeryResponse> requeryTx(@Body RequeryRequestBody body);

    @POST("/flwv3-pug/getpaidx/api/v2/verify")
    Call<String> requeryTx_v2(@Body RequeryRequestBodyv2 body);

    @GET("/flwv3-pug/getpaidx/api/flwpbf-banks.js?json=1")
    Call<String> getBanks();

    @POST("/flwv3-pug/getpaidx/api/tokenized/charge")
    Call<String> chargeToken(@Body Payload payload);

    @POST("/flwv3-pug/getpaidx/api/fee")
    Call<String> checkFee(@Body FeeCheckRequestBody body);

    @POST("/v2/gpx/devices/save")
    Call<String> saveCardToRave(@Body SaveCardRequestBody body);

    @POST("/v2/gpx/users/lookup")
    Call<String> lookupSavedCards(@Body LookupSavedCardsRequestBody requestBody);

    @POST("/v2/gpx/users/remove")
    Call<String> deleteSavedCard(@Body RemoveSavedCardRequestBody requestBody);

    @POST("/v2/gpx/users/send_otp")
    Call<String> sendRaveOtp(@Body SendOtpRequestBody requestBody);
}
