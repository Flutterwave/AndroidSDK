package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CardNoValidatorTest {

    CardNoValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new CardNoValidator();
    }

    @Test
    public void isCardNoValidator_isCorrectCardNumberPassed_returnsTrue() {
        String cardNo = "4242424242424242";
        boolean isCardNoValid = SUT.isCardNoStrippedValid(cardNo);
        assertThat(true, is(isCardNoValid));

    }


    @Test
    public void isCardNoValidator_isCardLessThan12NumberPassed_returnsFalse() {
        String cardNo = "42424242424";
        boolean isCardNoValid = SUT.isCardNoStrippedValid(cardNo);
        assertThat(false, is(isCardNoValid));

    }

    @Test
    public void isCardNoValidator_isEmptyCardNumberPassed_returnsFalse() {
        String cardNo = "";
        boolean isCardNoValid = SUT.isCardNoStrippedValid(cardNo);
        assertThat(false, is(isCardNoValid));

    }

    @Test
    public void isCardNoValidator_isNotNumberPassed_returnsFalse() {
        String cardNo = "cardNo";
        boolean isCardNoValid = SUT.isCardNoStrippedValid(cardNo);
        assertThat(false, is(isCardNoValid));

    }

}