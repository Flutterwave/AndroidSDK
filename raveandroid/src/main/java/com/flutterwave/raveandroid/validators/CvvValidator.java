package com.flutterwave.raveandroid.validators;

public class CvvValidator {

    public Boolean check(String cvv){
        return cvv.length() >= 3;
    }
}
