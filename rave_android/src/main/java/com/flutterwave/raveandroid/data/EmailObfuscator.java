package com.flutterwave.raveandroid.data;

import javax.inject.Inject;

public class EmailObfuscator {
    @Inject
    public EmailObfuscator() {

    }

    public String obfuscateEmail(String email) {
        int lastDotIndex = email.lastIndexOf(".");
        //String emailContent = email.substring(0, lastDotIndex - 1);


        String lastPart = email.substring(lastDotIndex - 1);
        String firstPart = email.substring(0, 2);

        String asterisks = "";

        for(int i = 2; i <= lastDotIndex - 2; i++){
            asterisks += "*";
        }

        return firstPart + asterisks + lastPart;
    }
}
