package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public boolean isAmountValid(Double amount) {
        return amount > 0;
    }

    public boolean isAmountValid(String amount) {

        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

    }
}
