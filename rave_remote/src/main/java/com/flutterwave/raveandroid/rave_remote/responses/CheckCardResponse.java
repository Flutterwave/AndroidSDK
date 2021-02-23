package com.flutterwave.raveandroid.rave_remote.responses;

public class CheckCardResponse {

    String Status;
    String Message;
    Data Data;
    Reference Reference;

    public Reference getReference() {
        return Reference;
    }

    public void setReference(Reference reference) {
        this.Reference = reference;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public Data getData() {
        return Data;
    }

    public void setData(Data data) {
        this.Data = data;
    }

    public static class Data {

        Number number;
        String scheme;
        String type;
        String brand;
        String prepaid;
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

        public String getPrepaid() {
            return prepaid;
        }

        public void setPrepaid(String prepaid) {
            this.prepaid = prepaid;
        }

        public Country getCountry() {
            return country;
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public Bank getBank() {
            return bank;
        }

        public void setBank(Bank bank) {
            this.bank = bank;
        }
    }

    public static class Reference {

    }

    public static class Bank {
        String name;
        String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

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
        String currency;
        int latitude;
        int longitude;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
