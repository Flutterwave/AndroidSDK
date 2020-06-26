package com.flutterwave.raveandroid.rave_remote.responses;

/**
 * Created by jeremiahVaris on 08/01/2019.
 */

public class LookupSavedCardsResponse {
    String status;
    String message;
    Data data[];

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public static class Data {
        String device; // Device fingerprint
        String mobile_number;
        String email;
        String card_hash;
        Card card;

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public void setMobile_number(String mobile_number) {
            this.mobile_number = mobile_number;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCard_hash() {
            return card_hash;
        }

        public void setCard_hash(String card_hash) {
            this.card_hash = card_hash;
        }

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }
    }

    public static class Card {
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
    }
}
