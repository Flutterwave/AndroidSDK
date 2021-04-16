package com.flutterwave.raveandroid.rave_presentation.data;

import android.util.Log;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import javax.inject.Inject;

public class PayloadToJson {

    public Gson gson;

    @Inject
    public PayloadToJson(Gson gson) {
        this.gson = gson;
    }

    public String convertChargeRequestPayloadToJson(Payload body) {

        Type type = new TypeToken<Payload>() {
        }.getType();
        String jsonString = gson.toJson(body, type);
        Log.d("Charge parameters",jsonString);
        return jsonString;
    }

}