package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NetworkValidatorTest {

    NetworkValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new NetworkValidator();
    }

    @Test
    public void isNetworkValid_isCorrectNetworkPassed_returnTrue() {
        String network = "mtn";
        boolean isNetworkValid = SUT.isNetworkValid(1);
        assertThat(true, is(isNetworkValid));
    }

    @Test
    public void isNetworkInValid_isEmptyPassed_returnFalse() {
        String network = "";
        boolean isNetworkValid = SUT.isNetworkValid(0);
        assertThat(false, is(isNetworkValid));
    }

    @Test
    public void isNetworkInValid_isNoNetworkPassed_returnFalse() {
        String network = "Select network";
        boolean isNetworkValid = SUT.isNetworkValid(0);
        assertThat(false, is(isNetworkValid));
    }

}