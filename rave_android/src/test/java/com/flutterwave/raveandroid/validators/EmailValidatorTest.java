package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class EmailValidatorTest {

    private EmailValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new EmailValidator();
    }

    @Test
    public void isEmailValid_isCorrectEmailPassed_returnsTrue() {
        String email = "test@flutterwave.com";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(true, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isEmptyUsernamePassed_returnsFalse() {
        String email = "@flutterwave.com";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isEmptyDomainPassed_returnsFalse() {
        String email = "test@.com";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isTopLevelLessThan2DomainPassed_returnsFalse() {
        String email = "test@flutterwave.c";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isTopLevelMoreThan6DomainPassed_returnsFalse() {
        String email = "test@flutterwave.commmmm";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isEmptyTopLevelDomainPassed_returnsFalse() {
        String email = "test@flutterwave";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isEmptyEmailPassed_returnsFalse() {
        String email = "";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }

    @Test
    public void isEmailValid_isNotEmailPassed_returnsFalse() {
        String email = "1./&$)";
        boolean isEmailValid = SUT.isEmailValid(email);
        assertThat(false, is(isEmailValid));
    }
}