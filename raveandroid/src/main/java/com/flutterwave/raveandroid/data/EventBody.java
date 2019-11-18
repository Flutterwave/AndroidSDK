package com.flutterwave.raveandroid.data;

import com.flutterwave.raveandroid.BuildConfig;

public class EventBody {
    String version = BuildConfig.VERSION_NAME;
    String publicKey;
    String language = "Android";
    String title;
    String message;

    EventBody(String publicKey, String title, String message) {
        this.publicKey = publicKey;
        this.title = title;
        this.message = message;
    }
}
