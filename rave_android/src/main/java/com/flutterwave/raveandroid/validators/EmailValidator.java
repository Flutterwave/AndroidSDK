package com.flutterwave.raveandroid.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class EmailValidator {

    @Inject
    public EmailValidator() {
    }

    public boolean isEmailValid(String email) {
        if (email != null) {
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            return matcher.find();
        } else {
            return false;
        }
    }
}
