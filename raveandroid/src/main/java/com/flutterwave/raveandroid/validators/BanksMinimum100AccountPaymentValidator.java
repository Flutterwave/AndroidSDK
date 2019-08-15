package com.flutterwave.raveandroid.validators;

public class BanksMinimum100AccountPaymentValidator {

    BankCodeValidator bankCodeValidator = new BankCodeValidator();
    public boolean isPaymentValid(String bankCode, Double amount) {

        boolean isBankCodeValid = bankCodeValidator.isBankCodeValid(bankCode);

        if (isBankCodeValid) {
            return (bankCode.equalsIgnoreCase("058") &&
                    bankCode.equalsIgnoreCase("011"))
                    || amount > 100;
        } else {
            return false;
        }
    }
}
