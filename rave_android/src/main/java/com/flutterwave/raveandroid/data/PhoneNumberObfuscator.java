package com.flutterwave.raveandroid.data;

import javax.inject.Inject;

public class PhoneNumberObfuscator {
    @Inject
    public PhoneNumberObfuscator() {

    }

    public String obfuscatePhoneNumber(String phonenumber) {
        int firstPartLength = phonenumber.length() - 8;
        int asterisksLength = phonenumber.length() - 4;


        String last4 = phonenumber.substring(phonenumber.length() - 4);
        String firstPart = "";
        String asterisks = "";
        if (firstPartLength > 1) {
            firstPart = phonenumber.substring(0, firstPartLength);
            asterisksLength = phonenumber.length() - 4 - firstPartLength;
        }

        for (int i = 0; i < asterisksLength; i++) {
            asterisks += "*";
        }
        return firstPart + asterisks + last4;
    }
}
