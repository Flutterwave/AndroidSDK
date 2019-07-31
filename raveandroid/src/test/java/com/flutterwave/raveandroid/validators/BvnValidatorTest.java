package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BvnValidatorTest {

    BvnValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new BvnValidator();
    }

    @Test
    public void isBvnValid_isCorrectBVNPassed_returnsTrue() {
        String bvn = "12345678901";
        boolean isBVNValid = SUT.isBvnValid(bvn);
        assertThat(true, is(isBVNValid));
    }

    @Test
    public void isBvnValid_isLessThan11Passed_returnsFalse() {
        String bvn = "1234567";
        boolean isBVNValid = SUT.isBvnValid(bvn);
        assertThat(false, is(isBVNValid));
    }

    @Test
    public void isBvnValid_ismoreThan11Passed_returnsFalse() {
        String bvn = "1234567890123";
        boolean isBVNValid = SUT.isBvnValid(bvn);
        assertThat(false, is(isBVNValid));
    }

    @Test
    public void isBvnValid_isEmptyPassed_returnsFalse() {
        String bvn = "";
        boolean isBVNValid = SUT.isBvnValid(bvn);
        assertThat(false, is(isBVNValid));
    }
}