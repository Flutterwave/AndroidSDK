package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UrlValidatorTest {

    UrlValidator SUT;

    @Before
    public void setUp() {
        SUT = new UrlValidator();
    }

    @Test
    public void isUrlValid_isCorrectUrlPassed_returnTrue() {
        String url = "http://www.rave.com";
        boolean isUrlValid = SUT.isUrlValid(url);
        assertThat(true, is(isUrlValid));
    }

    @Test
    public void isUrlValid_isWrongUrl_returnFalse() {
        String url = "hello";
        boolean isUrlValid = SUT.isUrlValid(url);
        assertThat(false, is(isUrlValid));
    }

    @Test
    public void isUrlValid_isEmptyPassed_returnFalse() {
        String url = "";
        boolean isUrlValid = SUT.isUrlValid(url);
        assertThat(false, is(isUrlValid));
    }

}