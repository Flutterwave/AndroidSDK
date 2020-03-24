package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by jeremiahVaris on 08/01/2019.
 */

public class SaveCardRequestBody {

    String processor_reference; // FlwRef
    String public_key;
    String device; // Device fingerprint
    String device_key; // User phone number
    String device_email;// User email

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public String getDevice_email() {
        return device_email;
    }

    public void setDevice_email(String device_email) {
        this.device_email = device_email;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getProcessor_reference() {
        return processor_reference;
    }

    public void setProcessor_reference(String processor_reference) {
        this.processor_reference = processor_reference;
    }


}
