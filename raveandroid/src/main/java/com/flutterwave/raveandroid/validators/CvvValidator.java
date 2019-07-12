package com.flutterwave.raveandroid.validators;

public class CvvValidator {

    public Boolean isCvvValid(String cvv){
        return cvv.length() >= 3;
    }
}
