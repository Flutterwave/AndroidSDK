package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public boolean isAmountValid(Double amount) {
        if (amount != null && !amount.toString().isEmpty()){
            return amount > 0;
        }
        else
        {
            return false;
        }
    }

    public boolean isAmountValid(String amount) {
        if (amount != null && !amount.isEmpty()) {
            return Double.valueOf(amount) > 0;
        }
        else
        {
            return false;
        }
    }
}
