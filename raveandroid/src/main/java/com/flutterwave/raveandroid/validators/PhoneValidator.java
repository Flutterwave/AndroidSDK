package com.flutterwave.raveandroid.validators;

public class PhoneValidator {

    public Boolean isPhoneValid(String phone){
        return phone.length() >= 1;
    }
}
