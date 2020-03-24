package com.flutterwave.raveandroid.rave_remote.responses;

/**
 * Created by jeremiahVaris on 08/01/2019.
 */

public class SendRaveOtpResponse {
    String status;
    String message;
    String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
