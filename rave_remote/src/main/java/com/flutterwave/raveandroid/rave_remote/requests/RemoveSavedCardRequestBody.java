package com.flutterwave.raveandroid.rave_remote.requests;


public class RemoveSavedCardRequestBody {

    private String card_hash;
    private String mobile_number;
    private String public_key;

    public RemoveSavedCardRequestBody( String card_hash, String mobile_number, String public_key){
        this.card_hash = card_hash;
        this.mobile_number = mobile_number;
        this.public_key = public_key;
    }

    public String getCard_hash() {
        return card_hash;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getPublic_key() {
        return public_key;
    }

}
