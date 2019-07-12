package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public Boolean isAmountValid(Double amount){
        return amount > 0;
    }
}
