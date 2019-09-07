package com.flutterwave.raveandroid;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

public class TransactionStatusChecker {

    private Gson gson;

    @Inject
    public TransactionStatusChecker(Gson gson) {
        this.gson = gson;
    }

    private boolean areAmountsSame(String amount1, String amount2) {
        Double number1 = Double.parseDouble(amount1);
        Double number2 = Double.parseDouble(amount2);

        return Math.abs(number1 - number2) < 0.0001;
    }

    public Boolean getTransactionStatus(String amount, String currency, String responseAsJSONString) {

        try {
            JsonObject jsonObject = gson.fromJson(responseAsJSONString, JsonObject.class);
            JsonObject jsonData = jsonObject.getAsJsonObject("data");
            String status = jsonData.get("status").getAsString();
            String txAmount = jsonData.get("amount").getAsString();
            String txCurrency = jsonData.get("currency").getAsString();
            String chargeResponse = jsonData.get("chargeResponseCode").getAsString();

            if (areAmountsSame(amount, txAmount) &&
                    chargeResponse.equalsIgnoreCase("00") &&
                    (status.contains("success") |
                            status.contains("pending-capture")) &&
                    currency.equalsIgnoreCase(txCurrency)) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
