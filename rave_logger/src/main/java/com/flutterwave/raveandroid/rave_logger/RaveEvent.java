package com.flutterwave.raveandroid.rave_logger;

public class RaveEvent {
    private String version; //version of the sdk
    private String publicKey;
    String language = "Android";
    private String title;
    private String message;

    public RaveEvent(String publicKey,
              String title,
              String message,
              String version) {
        this.publicKey = publicKey;
        this.title = title;
        this.message = message;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }
}
