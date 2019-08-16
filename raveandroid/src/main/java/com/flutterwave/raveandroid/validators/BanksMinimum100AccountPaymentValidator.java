package com.flutterwave.raveandroid.validators;

import javax.inject.Inject;

public class BanksMinimum100AccountPaymentValidator {

    BankCodeValidator bankCodeValidator;

    @Inject
    public BanksMinimum100AccountPaymentValidator() {
    }

    public BanksMinimum100AccountPaymentValidator(BankCodeValidator bankCodeValidator) {
        this.bankCodeValidator = bankCodeValidator;
    }


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
