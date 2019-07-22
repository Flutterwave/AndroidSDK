package com.flutterwave.raveandroid.validators;

public class AmountValidator {

    public Boolean isAmountValid(Double amount){
        if (amount != null && !amount.toString().isEmpty()){
            return amount > 0;
        }
        else
        {
            return false;
        }
    }

    public Boolean isAmountValid(String amount){
        if (amount != null && !amount.toString().isEmpty()){
            return Double.valueOf(amount) > 0;
        }
        else
        {
            return false;
        }
    }
}
