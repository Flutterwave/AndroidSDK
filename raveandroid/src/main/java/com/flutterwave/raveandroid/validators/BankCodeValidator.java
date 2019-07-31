package com.flutterwave.raveandroid.validators;

public class BankCodeValidator {

    public boolean isBankCodeValid(String bankCode) {

        boolean isValid = true;

        if (bankCode == null) {
            isValid = false;
        }
        return isValid;
    }
}
