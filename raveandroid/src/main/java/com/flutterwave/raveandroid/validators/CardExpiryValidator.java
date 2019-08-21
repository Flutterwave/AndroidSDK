package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class CardExpiryValidator {

    @Inject
    public CardExpiryValidator() {
    }

    public boolean isCardExpiryValid(String cardExpiry) {
        if (cardExpiry != null) {
            return Pattern.matches("\\d\\d/\\d\\d", cardExpiry);
        } else {
            return false;
        }

    }
}
