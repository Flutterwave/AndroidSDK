package com.flutterwave.raveandroid;

import javax.inject.Inject;

public class PhoneNumberObfuscator {
    @Inject
    public PhoneNumberObfuscator() {

    }

    public String obfuscatePhoneNumber(String phonenumber) {
        String first5 = phonenumber.substring(0, 5);
        String last4 = phonenumber.substring(phonenumber.length() - 4);
        String exes = "";
        for (int i = 0; i < phonenumber.length() - 9; i++) {
            exes += "*";
        }
        String obcuredPhoneNumber = first5 + exes + last4;
        return obcuredPhoneNumber;
    }
}
