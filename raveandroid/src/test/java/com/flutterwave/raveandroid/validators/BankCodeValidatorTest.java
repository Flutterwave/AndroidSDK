package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankCodeValidatorTest {

    BankCodeValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new BankCodeValidator();
    }

    @Test
    public void isBankCodeValid_notNull_returnsTrue() {
        String bankCode = "123";
        boolean isBankCodeValid = SUT.isBankCodeValid(bankCode);
        assertThat(true, is(isBankCodeValid));
    }

    @Test
    public void isBankCodeValid_is3DigitsPassed_returnsTrue() {
        String bankCode = "123";
        boolean isBankCodeValid = SUT.isBankCodeValid(bankCode);
        assertThat(true, is(isBankCodeValid));
    }

    @Test
    public void isBankCodeValid_isLessThan3Passed_returnsFalse() {
        String bankCode = "12";
        boolean isBankCodeValid = SUT.isBankCodeValid(bankCode);
        assertThat(false, is(isBankCodeValid));
    }

    @Test
    public void isBankCodeValid_isMoreThan3Passed_returnsTrue() {
        String bankCode = "1234";
        boolean isBankCodeValid = SUT.isBankCodeValid(bankCode);
        assertThat(false, is(isBankCodeValid));
    }

    @Test
    public void isBankCodeValid_isEmptyPassed_returnsTrue() {
        String bankCode = "";
        boolean isBankCodeValid = SUT.isBankCodeValid(bankCode);
        assertThat(false, is(isBankCodeValid));
    }
}