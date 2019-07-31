package com.flutterwave.raveandroid.validators;

public class BanksMinimum100AccountPaymentValidator {

    public boolean isPaymentValid(String bankCode, Double amount) {

        return (!bankCode.equalsIgnoreCase("058") &&
                !bankCode.equalsIgnoreCase("011"))
                || amount > 100;

    }
}
