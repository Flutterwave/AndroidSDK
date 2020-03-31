package com.flutterwave.raveandroid.rave_presentation.card;

public class RaveChargeCardPayload {
    private String pan;
    private String cvv;
    private String expiryDate;

    public RaveChargeCardPayload(String pan, String cvv, String expiryDate){
        this.pan = pan;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getPan() {
        return pan;
    }
}
