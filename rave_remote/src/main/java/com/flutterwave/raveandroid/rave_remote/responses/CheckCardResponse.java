package com.flutterwave.raveandroid.rave_remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckCardResponse {

    @Expose
    @SerializedName("transactionReference")
    private String transactionreference;
    @Expose
    @SerializedName("responseMessage")
    private String responsemessage;
    @Expose
    @SerializedName("responseCode")
    private String responsecode;
    @Expose
    @SerializedName("country")
    private String country;
    @Expose
    @SerializedName("nigeriancard")
    private boolean isNigerianCard;
    @Expose
    @SerializedName("cardName")
    private String cardname;
    @Expose
    @SerializedName("cardBin")
    private String cardbin;

    public String getTransactionreference() {
        return transactionreference;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public String getResponsecode() {
        return responsecode;
    }

    public String getCountry() {
        return country;
    }

    public boolean getNigerianCard() {
        return isNigerianCard;
    }

    public String getCardname() {
        return cardname;
    }

    public String getCardbin() {
        return cardbin;
    }
}

