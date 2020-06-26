package com.flutterwave.raveandroid.rave_presentation.data.validators;

import com.flutterwave.raveandroid.rave_presentation.data.Utils;

import javax.inject.Inject;

public class CardNoValidator {

    @Inject
    public CardNoValidator() {
    }

    public boolean isCardNoStrippedValid(String cardNoStripped) {
        try {
            Long.valueOf(cardNoStripped);
            return !(cardNoStripped.length() < 12) && Utils.isValidLuhnNumber(cardNoStripped);
        } catch (Exception e) {
            return false;
        }

    }
}
