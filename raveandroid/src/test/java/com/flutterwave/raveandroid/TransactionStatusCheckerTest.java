package com.flutterwave.raveandroid;

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

    @Test
    public void getTransactionStatus_isInValidAmountPassed_returnFalse() {

        JsonObject jsonObject = generateJSONObjectResponse("99", "NGN", true);
        JsonObject jsonObjectData = jsonObject.get("data").getAsJsonObject();

        boolean status = transactionStatusChecker.getTransactionStatus(jsonObject.toString());

        Assert.assertEquals("success", jsonObjectData.get("status").getAsString());
        Assert.assertNotEquals("90", jsonObjectData.get("amount").getAsString());
        Assert.assertEquals("NGN", jsonObjectData.get("currency").getAsString());
        Assert.assertEquals("00", jsonObjectData.get("chargeResponseCode").getAsString());

        assertThat(status, is(false));
    }

    @Test
    public void getTransactionStatus_isInValidCurrencyPassed_returnFalse() {

        JsonObject jsonObject = generateJSONObjectResponse("100", "EUR", true);
        JsonObject jsonObjectData = jsonObject.get("data").getAsJsonObject();

        boolean status = transactionStatusChecker.getTransactionStatus(jsonObject.toString());

        Assert.assertEquals("success", jsonObjectData.get("status").getAsString());
        Assert.assertEquals("100", jsonObjectData.get("amount").getAsString());
        Assert.assertNotEquals("USD", jsonObjectData.get("currency").getAsString());
        Assert.assertEquals("00", jsonObjectData.get("chargeResponseCode").getAsString());

        assertThat(status, is(false));
    }


    @Test
    public void getTransactionStatus_isInValidJsonResponsePassed_returnFalse() {

        JsonObject jsonObject = generateJSONObjectResponse("100", "NGN", false);

        boolean status = transactionStatusChecker.getTransactionStatus(jsonObject.toString());

        assertThat(status, is(false));
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