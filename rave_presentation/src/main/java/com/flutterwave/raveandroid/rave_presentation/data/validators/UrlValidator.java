package com.flutterwave.raveandroid.rave_presentation.data.validators;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class UrlValidator {

    @Inject
    public UrlValidator() {

    }

    public boolean isUrlValid(String url) {
        return Pattern.matches("^(https?|ftp|file|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", url);
    }
}
