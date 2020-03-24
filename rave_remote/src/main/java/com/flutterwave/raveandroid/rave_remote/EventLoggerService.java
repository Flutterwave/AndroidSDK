package com.flutterwave.raveandroid.rave_remote;

import com.flutterwave.raveandroid.rave_remote.requests.EventBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventLoggerService {
    @POST("/staging/sendevent")
    Call<String> logEvent(@Body EventBody body);
}
