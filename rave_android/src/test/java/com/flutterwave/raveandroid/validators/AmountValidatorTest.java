package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class AmountValidatorTest {

    AmountValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new AmountValidator();
    }

    @Test
    public void isAmountValid_isCorrectAmountPassed_returnsTrue() {
        Double amount = 123.0;
        boolean isAmountValid = SUT.isAmountValid(amount);
        assertThat(true, is(isAmountValid));
    }

    @Test
    public void isAmountValid_isNegativeAmountPassed_returnsFalse() {
        Double amount = -1.0;
        boolean isAmountValid = SUT.isAmountValid(amount);
        assertThat(false, is(isAmountValid));
    }

    @Test
    public void isAmountValid_isLetterPassed_returnsFalse() {
        String amount = "accd";
        boolean isAmountValid = SUT.isAmountValid(amount);
        assertThat(false, is(isAmountValid));
    }

    @Test
    public void isAmountValid_isEmptyAmountPassed_returnsFalse() {
        String amount = "";
        boolean isAmountValid = SUT.isAmountValid(amount);
        assertThat(false, is(isAmountValid));
    }

}