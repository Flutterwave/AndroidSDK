package com.flutterwave.raveandroid.rave_remote.responses;

/**
 * Created by hfetuga on 28/06/2018.
 */

public class RequeryResponsev2 {

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

    String status;
    String message;
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        String txref;
        String flwref;

        public String getTxref() {
            return txref;
        }

        public void setTxref(String txref) {
            this.txref = txref;
        }

        public String getFlwref() {
            return flwref;
        }

        public void setFlwref(String flwref) {
            this.flwref = flwref;
        }

        public String getChargecode() {
            return chargecode;
        }

        public void setChargecode(String chargecode) {
            this.chargecode = chargecode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        String chargecode;
        String status;
    }
}
