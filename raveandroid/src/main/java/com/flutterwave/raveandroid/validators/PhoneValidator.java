package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class PhoneValidator {

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
