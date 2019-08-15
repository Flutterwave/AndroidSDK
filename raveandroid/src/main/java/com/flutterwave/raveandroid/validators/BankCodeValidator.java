package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class BankCodeValidator {

    public boolean isBankCodeValid(String bankCode) {

        if (bankCode == null) {
            return false;
        } else {
            return Pattern.matches("\\d{3}", bankCode);
        }
    }
}
