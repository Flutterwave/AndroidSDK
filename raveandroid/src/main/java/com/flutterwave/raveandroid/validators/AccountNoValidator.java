package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class AccountNoValidator {

    public boolean isAccountNumberValid(String accountNo) {
        if (accountNo != null) {
            return Pattern.matches("\\d{10}", accountNo);
        } else {
            return false;
        }
    }
}
