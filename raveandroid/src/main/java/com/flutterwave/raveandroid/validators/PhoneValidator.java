package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class PhoneValidator {

    @Inject
    public PhoneValidator() {
    }

    public boolean isPhoneValid(String phone) {
        if (phone != null) {
            if (phone.length() > 1) {
                return Pattern.matches("^[0-9]*$", phone);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
