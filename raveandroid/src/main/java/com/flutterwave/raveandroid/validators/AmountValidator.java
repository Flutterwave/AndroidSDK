package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public boolean isAmountValid(Double amount) {
        return amount > 0;
    }
}
