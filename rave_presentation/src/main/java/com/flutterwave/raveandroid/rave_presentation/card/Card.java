package com.flutterwave.raveandroid.rave_presentation.card;

public class Card {
    private String cardNumber;
    private String expiryYear;
    private String cvv;
    private String expiryMonth;

    public Card(String cardNumber, String expiryMonth, String expiryYear, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.expiryMonth = expiryMonth;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

}
