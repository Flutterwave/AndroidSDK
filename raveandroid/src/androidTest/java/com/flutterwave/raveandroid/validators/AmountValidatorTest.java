package com.flutterwave.raveandroid.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AmountValidatorTest {

    AmountValidator AVT;

    @Before
    public void setUp() throws Exception {
        AVT = new AmountValidator();
    }

    @Test
    public void isAmountValids() {
        boolean result = AVT.isAmountValid(0.0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAmountValids2() {
        boolean result = AVT.isAmountValid(-3.0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAmountValids3() {
        boolean result = AVT.isAmountValid(2.0);
        Assert.assertThat(result, is(true));
    }
}