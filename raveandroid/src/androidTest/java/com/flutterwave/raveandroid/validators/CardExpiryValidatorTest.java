package com.flutterwave.raveandroid.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CardExpiryValidatorTest {

    CardExpiryValidator CEV;

    @Before
    public void setUp() throws Exception {
        CEV = new CardExpiryValidator();
    }

    @Test
    public void isCardExpiryValid() {
        boolean result = CEV.isCardExpiryValid("hello");
        Assert.assertThat(result, is(true));
    }
}