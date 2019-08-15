package com.flutterwave.raveandroid.validators;

public class CvvValidator {

    public boolean isCvvValid(String cvv) {

        try {
            Integer.parseInt(cvv);
            return cvv.length() == 3 || cvv.length() == 4;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
