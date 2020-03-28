package com.flutterwave.raveandroid.rave_presentation.data;

public class AddressDetails {
    private final String streetAddress;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;

    public AddressDetails(String streetAddress, String city, String state, String zipCode, String country) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }
}
