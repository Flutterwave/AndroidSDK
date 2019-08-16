package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class AccountNoValidator {

    @Inject
    public AccountNoValidator() {

    }

    public boolean isAccountNumberValid(String accountNo) {
        if (accountNo != null) {
            return Pattern.matches("\\d{10}", accountNo);
        } else {
            return false;
        }
    }
}
