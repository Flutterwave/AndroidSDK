package com.flutterwave.raveandroid.validators;

public class CardExpiryValidator {

    public boolean isCardExpiryValid(String cardExpiry){
        return cardExpiry.length() == 5;
    }
}
