package com.flutterwave.raveandroid.responses;

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

    public static class Data {

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
