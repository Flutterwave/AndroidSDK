package com.flutterwave.raveandroid.rave_remote.requests;


public class EventBody {
    String publicKey;
    String language = "Android";
    String title;
    String message;

    public EventBody(String publicKey, String title, String message) {
        this.publicKey = publicKey;
        this.title = title;
        this.message = message;
    }
}
