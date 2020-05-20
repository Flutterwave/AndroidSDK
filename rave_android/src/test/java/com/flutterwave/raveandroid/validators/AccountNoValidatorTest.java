package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccountNoValidatorTest {

    AccountNoValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new AccountNoValidator();
    }

    @Test
    public void isAccountNumberValid_isCorrectAccounNoPassed_returnsTrue() {
        String accountNo = "1234567890";
        boolean isAccountNoValid = SUT.isAccountNumberValid(accountNo);
        assertThat(true, is(isAccountNoValid));
    }

    @Test
    public void isAccountNumberValid_isLettersPassed_returnsFalse() {
        String accountNo = "account";
        boolean isAccountNoValid = SUT.isAccountNumberValid(accountNo);
        assertThat(false, is(isAccountNoValid));
    }

    @Test
    public void isAccountNumberValid_isLessThan10Passed_returnsFalse() {
        String accountNo = "12345";
        boolean isAccountNoValid = SUT.isAccountNumberValid(accountNo);
        assertThat(false, is(isAccountNoValid));
    }

    @Test
    public void isAccountNumberValid_isMoreThan10Passed_returnsFalse() {
        String accountNo = "1234567890123";
        boolean isAccountNoValid = SUT.isAccountNumberValid(accountNo);
        assertThat(false, is(isAccountNoValid));
    }

    @Test
    public void isAccountNumberValid_isEmptyPassed_returnsFalse() {
        String accountNo = "";
        boolean isAccountNoValid = SUT.isAccountNumberValid(accountNo);
        assertThat(false, is(isAccountNoValid));
    }
}