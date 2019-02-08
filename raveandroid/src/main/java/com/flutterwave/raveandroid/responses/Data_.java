package com.flutterwave.raveandroid.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data_ {

    @SerializedName("responsecode")
    @Expose
    private String responsecode;
    @SerializedName("responsetoken")
    @Expose
    private String responsetoken;
    @SerializedName("responsemessage")
    @Expose
    private String responsemessage;

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getResponsetoken() {
        return responsetoken;
    }

    public void setResponsetoken(String responsetoken) {
        this.responsetoken = responsetoken;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

}
