package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public boolean isAmountValid(Double amount) {
        return amount > 0;
    }

    public boolean isAmountValid(String amount) {
        if (!amount.isEmpty()) {
            return isAmountValid(Double.valueOf(amount));
        } else {
            return false;
        }
    }
}
