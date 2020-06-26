package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CvvValidatorTest {

    private CvvValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new CvvValidator();
    }

    @Test
    public void isCvvValid_CorrectCvvPassed_returnsTrue() {
        String cvv = "345";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(true, is(isValid));
    }

    @Test
    public void isCvvValid_4digitCvvPassed_returnsTrue() {
        String cvv = "3455";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(true, is(isValid));
    }

    @Test
    public void isCvvValid_CvvPassedLongerThan4_returnsFalse() {
        String cvv = "34554";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(false, is(isValid));
    }

    @Test
    public void isCvvValid_CvvPassedLessThan3_returnsFalse() {
        String cvv = "34";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(false, is(isValid));
    }

    @Test
    public void isCvvValid_CvvPassedIsNotDigits_returnsFalse() {
        String cvv = "aas";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(false, is(isValid));
    }

    @Test
    public void isCvvValid_CvvPassedIsEmpty_returnsFalse() {
        String cvv = "";
        boolean isValid = SUT.isCvvValid(cvv);
        assertThat(false, is(isValid));
    }

}