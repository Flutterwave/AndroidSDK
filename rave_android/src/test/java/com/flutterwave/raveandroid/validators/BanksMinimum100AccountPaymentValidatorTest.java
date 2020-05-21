package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BanksMinimum100AccountPaymentValidatorTest {


    BanksMinimum100AccountPaymentValidator SUT;
    BankCodeValidator bankCodeValidator;


    @Before
    public void setUp() throws Exception {
        bankCodeValidator = new BankCodeValidator();
        SUT = new BanksMinimum100AccountPaymentValidator(bankCodeValidator);
    }

    @Test
    public void isPaymentValid_isCorrectBankCodePassed_returnsTrue() {
        String bankCode = "123";
        Double amount = 101.0;

        boolean isPaymentValid = SUT.isPaymentValid(bankCode, amount);
        assertThat(true, is(isPaymentValid));
    }

    @Test
    public void isPaymentValid_isWrongBankCodePassed_returnsFalse() {
        String bankCode = "abc";
        Double amount = 101.0;

        boolean isPaymentValid = SUT.isPaymentValid(bankCode, amount);
        assertThat(false, is(isPaymentValid));
    }

    @Test
    public void isPaymentValid_isEmptyBankCodePassed_returnsFalse() {
        String bankCode = "";
        Double amount = 101.0;

        boolean isPaymentValid = SUT.isPaymentValid(bankCode, amount);
        assertThat(false, is(isPaymentValid));
    }

    @Test
    public void isPaymentValid_isAmountEqualTo100ValidCodePassed_returnsFalse() {
        String bankCode = "058";
        Double amount = 100.0;

        boolean isPaymentValid = SUT.isPaymentValid(bankCode, amount);
        assertThat(false, is(isPaymentValid));
    }

    @Test
    public void isPaymentValid_isAmountLessThan100ValidCodePassed_returnsFalse() {
        String bankCode = "058";
        Double amount = 1.0;

        boolean isPaymentValid = SUT.isPaymentValid(bankCode, amount);
        assertThat(false, is(isPaymentValid));
    }
}