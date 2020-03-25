package com.flutterwave.raveandroid.rave_remote.responses;

import java.util.List;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryResponse {

    String status;

    public Data getData() {
        return data;
    }

    Data data;

    public String getStatus() {
        return status;
    }


    public void setData(Data data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data {

        Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        String chargeResponseCode;

        public String getChargeResponseCode() { return chargeResponseCode; }

        public CardDets getCard() {
            return card;
        }

        CardDets card;

        String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setChargeResponseCode(String chargeResponseCode) {
            this.chargeResponseCode = chargeResponseCode;
        }
    }

    public static class Card_tokens
    {
        private String embedtoken;

        private String shortcode;

        public String getEmbedtoken ()
        {
            return embedtoken;
        }

        public String getShortcode ()
        {
            return shortcode;
        }

    }

    public static class CardDets
    {
        private String cardBIN;

        private List<Card_tokens> card_tokens;

        private String brand;

        public String getCardBIN ()
        {
            return cardBIN;
        }

        public void setCardBIN (String cardBIN)
        {
            this.cardBIN = cardBIN;
        }

        public List<Card_tokens> getCard_tokens ()
        {
            return card_tokens;
        }

        public void setCard_tokens (List<Card_tokens> card_tokens)
        {
            this.card_tokens = card_tokens;
        }

        public String getBrand ()
        {
            return brand;
        }

        public void setBrand (String brand)
        {
            this.brand = brand;
        }
    }
}
