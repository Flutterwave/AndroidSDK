package com.flutterwave.raveandroid;

import javax.inject.Inject;

public class Encrypt {

    private final String ALGORITHM = "DESede";
    private final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

    String data;
    String key;

    @Inject
    public Encrypt(String data, String key) {
        this.data = data;
        this.key = key;
    }

    @Inject
    public String providesEncrypt() {

        try {
            return "";
        } catch (Exception e) {
            return null;
        }

    }
}
