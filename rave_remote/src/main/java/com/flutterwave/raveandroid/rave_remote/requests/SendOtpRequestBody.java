package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by jeremiahVaris on 08/01/2019.
 */

public class SendOtpRequestBody {
    String public_key;
    String device_key;
    String card_hash;

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public String getCard_hash() {
        return card_hash;
    }

    public void setCard_hash(String card_hash) {
        this.card_hash = card_hash;
    }
}
