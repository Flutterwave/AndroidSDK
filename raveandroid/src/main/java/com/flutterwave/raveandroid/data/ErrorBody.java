package com.flutterwave.raveandroid.data;

/**
 * Created by hamzafetuga on 19/07/2017.
 */

public class ErrorBody {
    public ErrorBody(String status, String message) {
        this.status = status;
        this.message = message;
    }

    String status;

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

    String message;

    public Data getData() {
        return data;
    }

    Data data;


//    String data;

    public static class Data {
        boolean is_error;

        public String getCode() {
            return code;
        }

        String code;
    }
}

