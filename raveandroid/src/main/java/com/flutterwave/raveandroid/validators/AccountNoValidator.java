package com.flutterwave.raveandroid.validators;

public class AccountNoValidator {

    public boolean isAccountNumberValid(String accNo) {
        boolean isValid = true;

        if (!(accNo != null && accNo.length() == 10)) {
            isValid = false;
        }

        return isValid;
    }
}
