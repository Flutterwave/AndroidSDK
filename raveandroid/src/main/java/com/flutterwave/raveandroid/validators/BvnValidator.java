package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class BvnValidator {

    public boolean isBvnValid(String bvn) {
        if (bvn != null) {
            return Pattern.matches("\\d{11}", bvn);
        } else {
            return false;
        }
    }
}
