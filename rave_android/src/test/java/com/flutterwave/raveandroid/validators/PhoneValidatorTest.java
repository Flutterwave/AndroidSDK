package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PhoneValidatorTest {

    PhoneValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new PhoneValidator();
    }

    @Test
    public void isPhoneValid_isCorrectPhonePassed_returnTrue() {
        String phone = "08012345678";
        boolean isPhoneValid = SUT.isPhoneValid(phone);
        assertThat(true, is(isPhoneValid));
    }

    @Test
    public void isPhoneValid_isNoneDigitsPassed_returnFalse() {
        String phone = "hello";
        boolean isPhoneValid = SUT.isPhoneValid(phone);
        assertThat(false, is(isPhoneValid));
    }

    @Test
    public void isPhoneValid_isEmptyPassed_returnFalse() {
        String phone = "";
        boolean isPhoneValid = SUT.isPhoneValid(phone);
        assertThat(false, is(isPhoneValid));
    }

}