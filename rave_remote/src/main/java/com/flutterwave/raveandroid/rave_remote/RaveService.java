package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RaveService {
    @POST("/flwv3-pug/getpaidx/api/charge")
    Call<String> charge(@Body ChargeRequestBody body);
}
