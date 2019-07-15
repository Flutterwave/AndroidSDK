package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.card.CardFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CvvValidatorTest {

    CvvValidator cvvValidator;
    @Before
    public void setUp() throws Exception {
        cvvValidator = new CvvValidator();
    }

    @Test
    public void isCvvValid() {
        Boolean result = cvvValidator.isCvvValid("fed");
        Assert.assertThat(result, is(true));
    }
}