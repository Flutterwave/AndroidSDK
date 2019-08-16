package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.Utils;

import javax.inject.Inject;

public class CardNoValidator {

    @Inject
    public CardNoValidator() {
    }

    public boolean isCardNoStrippedValid(String cardNoStripped) {
        try {
            Long.valueOf(cardNoStripped);
            return !(cardNoStripped.length() < 12 | !Utils.isValidLuhnNumber(cardNoStripped));
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
