package com.flutterwave.raveandroid;

import org.json.JSONObject;

import javax.inject.Inject;

public class TransactionStatusChecker {

    @Inject
    public TransactionStatusChecker() {
    }

    private static boolean areAmountsSame(String amount1, String amount2) {
        Double number1 = Double.parseDouble(amount1);
        Double number2 = Double.parseDouble(amount2);

        return Math.abs(number1 - number2) < 0.0001;
    }

    public Boolean getTransactionStatus(RavePayInitializer ravePayInitializer, String responseAsJSONString) {
        String amount = ravePayInitializer.getAmount() + "";
        String currency = ravePayInitializer.getCurrency();

        try {
            JSONObject jsonObject = new JSONObject(responseAsJSONString);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            String status = jsonData.getString("status");
            String txAmount = jsonData.getString("amount");
            String txCurrency = jsonData.getString("currency");
            String chargeResponse = jsonData.getString("chargeResponseCode");

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
