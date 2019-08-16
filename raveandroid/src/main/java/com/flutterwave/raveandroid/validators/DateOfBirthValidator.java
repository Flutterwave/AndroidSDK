package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class DateOfBirthValidator {

    @Inject
    public DateOfBirthValidator() {
    }

    public boolean isDateValid(String dateOfBirth) {
        if (dateOfBirth != null) {
            return Pattern.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d", dateOfBirth);
        } else {
            return false;
        }
    }
}
