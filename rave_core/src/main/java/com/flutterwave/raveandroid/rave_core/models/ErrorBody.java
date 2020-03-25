package com.flutterwave.raveandroid.rave_core.models;

/**
 * Created by hamzafetuga on 19/07/2017.
 */

public class ErrorBody {
    String status;
    String message;
    Data data;

    public ErrorBody(String status, String message) {
        this.status = status;
        this.message = message;
    }

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

    public Data getData() {
        return data;
    }


//    String data;

    public static class Data {
        boolean is_error;
        String code;

        public String getCode() {
            return code;
        }
    }
}

