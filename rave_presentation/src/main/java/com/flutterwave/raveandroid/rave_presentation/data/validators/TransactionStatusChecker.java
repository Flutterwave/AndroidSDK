package com.flutterwave.raveandroid.rave_presentation.data.validators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

public class TransactionStatusChecker {

    public Gson gson;

    @Inject
    public TransactionStatusChecker(Gson gson) {
        this.gson = gson;
    }

    public Boolean getTransactionStatus(String responseAsJSONString) {
        try {
            JsonObject jsonObject = gson.fromJson(responseAsJSONString, JsonObject.class);
            JsonObject jsonData = jsonObject.getAsJsonObject("data");
            String chargeResponse = jsonData.get("chargeResponseCode").getAsString();

            if (chargeResponse.equalsIgnoreCase("00")) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
