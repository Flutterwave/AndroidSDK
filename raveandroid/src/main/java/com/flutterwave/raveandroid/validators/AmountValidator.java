package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public Boolean check(String amount){
        return Double.parseDouble(amount) > 0;
    }
}
