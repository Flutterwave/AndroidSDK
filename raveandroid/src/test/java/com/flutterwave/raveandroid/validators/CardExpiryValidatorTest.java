package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CardExpiryValidatorTest {

    private CardExpiryValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new CardExpiryValidator();
    }

    @Test
    public void isValidCardExpiry_IsCorrectCardExpiryPassed_returnsTrue() {
        String cardExpiry = "12/11";
        boolean isCardExpiryValid = SUT.isCardExpiryValid(cardExpiry);
        assertThat(true, is(isCardExpiryValid));
    }

    @Test
    public void isValidCardExpiry_IsLettersCardExpiryPassed_returnsFalse() {
        String cardExpiry = "as/11";
        boolean isCardExpiryValid = SUT.isCardExpiryValid(cardExpiry);
        assertThat(false, is(isCardExpiryValid));
    }

    @Test
    public void isValidCardExpiry_IsEmptyCardExpiryPassed_returnsFalse() {
        String cardExpiry = "";
        boolean isCardExpiryValid = SUT.isCardExpiryValid(cardExpiry);
        assertThat(false, is(isCardExpiryValid));
    }

}