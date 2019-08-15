package com.flutterwave.raveandroid.validators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateOfBirthValidatorTest {

    DateOfBirthValidator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new DateOfBirthValidator();
    }

    @Test
    public void isDateValid_isCorrectDatePassed_returnsTrue() {

        String dateOfBirth = "31/07/2019";
        boolean isValidDate = SUT.isDateValid(dateOfBirth);
        assertThat(true, is(isValidDate));
    }

    @Test
    public void isDateValid_isLettersPassed_returnsFalse() {

        String dateOfBirth = "date";
        boolean isValidDate = SUT.isDateValid(dateOfBirth);
        assertThat(false, is(isValidDate));
    }

    @Test
    public void isDateValid_isWrongPatternPassed_returnsFalse() {

        String dateOfBirth = "12121992";
        boolean isValidDate = SUT.isDateValid(dateOfBirth);
        assertThat(false, is(isValidDate));
    }

    @Test
    public void isDateValid_isEmptyPassed_returnsFalse() {

        String dateOfBirth = "";
        boolean isValidDate = SUT.isDateValid(dateOfBirth);
        assertThat(false, is(isValidDate));
    }

}