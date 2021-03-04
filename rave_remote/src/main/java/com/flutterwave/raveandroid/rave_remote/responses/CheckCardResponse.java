package com.flutterwave.raveandroid.rave_remote.responses;

import androidx.annotation.Nullable;

public class CheckCardResponse {

    Number number;
    String scheme;
    String type;
    String brand;
    boolean prepaid;
    @Nullable
    Country country;
    Bank bank;

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(boolean prepaid) {
        this.prepaid = prepaid;
    }

    @Nullable
    public Country getCountry() {
        return country;
    }

    public void setCountry(@Nullable Country country) {
        this.country = country;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public static class Bank { }

    public static class Number {
        int length;
        boolean luhn;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public boolean isLuhn() {
            return luhn;
        }

        public void setLuhn(boolean luhn) {
            this.luhn = luhn;
        }
    }

    public static class Country {
        String numeric;
        String alpha2;
        String name;
        String emoji;
        @Nullable
        String currency;
        int latitude;
        int longitude;

        @Nullable
        public String getCurrency() {
            return currency;
        }

        public void setCurrency(@Nullable String currency) {
            this.currency = currency;
        }

        public String getAlpha2() {
            return alpha2;
        }

        public void setAlpha2(String alpha2) {
            this.alpha2 = alpha2;
        }
    }

}

