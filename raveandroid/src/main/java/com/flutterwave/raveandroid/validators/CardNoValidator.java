package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.Utils;

public class CardNoValidator {

    public boolean isCardNoStrippedValid(String cardNoStripped){
        return !(cardNoStripped.length() < 12 | !Utils.isValidLuhnNumber(cardNoStripped));
    }
}
