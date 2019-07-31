package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class DateOfBirthValidator {

    public boolean isDateValid(String dateOfBirth) {
        if (dateOfBirth != null) {
            return Pattern.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d", dateOfBirth);
        } else {
            return false;
        }
    }
}
