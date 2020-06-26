package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by jeremiahVaris on 08/01/2019.
 */

public class LookupSavedCardsRequestBody {
    String public_key;
    String device_key; //User phone number

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


}
