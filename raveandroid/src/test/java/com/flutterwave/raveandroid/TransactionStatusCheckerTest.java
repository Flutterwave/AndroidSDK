package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TransactionStatusCheckerTest {

    private TransactionStatusChecker transactionStatusChecker;

    @Before
    public void setUp() {
        transactionStatusChecker = new TransactionStatusChecker(new Gson());
    }

    @Test
    public void getTransactionStatus_isValidParametersPassed_returnTrue() {

        JsonObject jsonObject = generateJSONObjectResponse("100", "NGN", true);
        JsonObject jsonObjectData = jsonObject.get("data").getAsJsonObject();

        boolean status = transactionStatusChecker.getTransactionStatus(jsonObject.toString());

        Assert.assertEquals("success", jsonObjectData.get("status").getAsString());
        Assert.assertEquals("100", jsonObjectData.get("amount").getAsString());
        Assert.assertEquals("NGN", jsonObjectData.get("currency").getAsString());
        Assert.assertEquals("00", jsonObjectData.get("chargeResponseCode").getAsString());

        assertThat(status, is(true));
    }

    private JsonObject generateJSONObjectResponse(String amount, String currency, boolean isValidJson) {
        String jsonString;
        if (isValidJson) {
            jsonString = "{\"data\":{\"status\": \"success\",\"amount\": \"" + amount + "\",\"currency\": \"" + currency + "\",\"chargeResponseCode\": \"00\"}}";
        } else {
            jsonString = "{\"data\":{\"amount\": \"" + amount + "\",\"currency\": \"" + currency + "\",\"chargeResponseCode\": \"00\"}}";
        }
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }

}