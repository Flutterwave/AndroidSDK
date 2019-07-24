package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class CardExpiryValidator {

    public boolean isCardExpiryValid(String cardExpiry){
        return Pattern.matches("\\d\\d/\\d\\d", cardExpiry);
    }
}
