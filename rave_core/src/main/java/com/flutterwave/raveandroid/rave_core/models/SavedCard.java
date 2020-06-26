package com.flutterwave.raveandroid.rave_core.models;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class SavedCard {

    String cardHash;
    String email;
    String masked_pan;
    String card_brand;

    public String getMasked_pan() {
        return masked_pan;
    }

    public void setMasked_pan(String masked_pan) {
        this.masked_pan = masked_pan;
    }

    public String getCard_brand() {
        return card_brand;
    }

    public void setCard_brand(String card_brand) {
        this.card_brand = card_brand;
    }


    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
