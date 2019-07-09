package com.flutterwave.raveandroid.validators;

public class CardExpiryValidator {

    public Boolean check(String cardExpiry){
        return cardExpiry.length() == 5;
    }
}
