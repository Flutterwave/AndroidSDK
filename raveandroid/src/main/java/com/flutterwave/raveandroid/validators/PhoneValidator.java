package com.flutterwave.raveandroid.validators;

public class PhoneValidator {

    public boolean isPhoneValid(String phone){
        return phone.length() >= 1;
    }
}
