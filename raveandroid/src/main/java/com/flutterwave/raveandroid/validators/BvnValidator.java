package com.flutterwave.raveandroid.validators;

public class BvnValidator {

    public boolean isBvnValid(String bvn) {
        boolean isValid = true;

        if (!(bvn != null && bvn.length() == 11)) {
            isValid = false;
        }

        return isValid;
    }
}
