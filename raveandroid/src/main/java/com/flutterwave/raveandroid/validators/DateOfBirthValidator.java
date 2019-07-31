package com.flutterwave.raveandroid.validators;

public class DateOfBirthValidator {

    public boolean isDateValid(String dateOfBirth) {

        boolean isValid = true;

        if (!(dateOfBirth != null && dateOfBirth.length() == 10)) {
            return false;
        }
        return isValid;
    }
}
