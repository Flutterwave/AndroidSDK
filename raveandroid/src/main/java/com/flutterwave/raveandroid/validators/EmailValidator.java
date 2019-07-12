package com.flutterwave.raveandroid.validators;

public class EmailValidator {

    public Boolean isEmailValid(String email) {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
