package com.flutterwave.raveandroid.validators;

public class CardExpiryValidator {

    public Boolean isCardExpiryValid(String cardExpiry){
        return cardExpiry.length() == 5;
    }
}
