package com.flutterwave.raveandroid.rave_logger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoggerService {
    @POST("/staging/sendevent")
    Call<String> logEvent(@Body Event body);
}
