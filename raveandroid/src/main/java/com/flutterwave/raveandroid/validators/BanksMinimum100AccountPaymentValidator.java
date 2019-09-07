package com.flutterwave.raveandroid.validators;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class BanksMinimum100AccountPaymentValidator {

    @Inject
    BankCodeValidator bankCodeValidator;

    private List<String> banksInFocus = Arrays.asList("058", "011");

    public BanksMinimum100AccountPaymentValidator() {
    }

    @Inject
    public BanksMinimum100AccountPaymentValidator(BankCodeValidator bankCodeValidator) {
        this.bankCodeValidator = bankCodeValidator;
    }

    public boolean isPaymentValid(String bankCode, Double amount) {

        boolean isBankCodeValid = bankCodeValidator.isBankCodeValid(bankCode);

        if (isBankCodeValid) {
            if (!banksInFocus.contains(bankCode)) {
                return true;
            } else {
                return amount > 100;
            }

        } else {
            return false;
        }
    }
}
