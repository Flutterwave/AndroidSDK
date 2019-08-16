package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class BvnValidator {

    @Inject
    public BvnValidator() {
    }

    public boolean isBvnValid(String bvn) {
        if (bvn != null) {
            return Pattern.matches("\\d{11}", bvn);
        } else {
            return false;
        }
    }
}
