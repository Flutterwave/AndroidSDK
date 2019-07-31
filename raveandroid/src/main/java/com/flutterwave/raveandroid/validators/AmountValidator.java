package com.flutterwave.raveandroid.validators;

import java.util.regex.Pattern;

public class AmountValidator {

    public boolean isAmountValid(Double amount) {
        return amount > 0;
    }

    public boolean isAmountValid(String amount) {

        if (amount != null && !amount.isEmpty()) {
            return Pattern.matches("^[0-9]*$", amount);
        } else {
            return false;
        }

    }
}
