package com.flutterwave.raveandroid.validators;

public class EmailValidator {

    public boolean isEmailValid(String email) {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
