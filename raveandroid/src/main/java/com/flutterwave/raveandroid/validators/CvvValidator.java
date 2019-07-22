package com.flutterwave.raveandroid.validators;

public class CvvValidator {

    public boolean isCvvValid(String cvv){
        return cvv.length() >= 3;
    }
}
