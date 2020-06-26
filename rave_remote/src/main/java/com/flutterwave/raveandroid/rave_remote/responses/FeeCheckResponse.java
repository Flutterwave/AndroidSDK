package com.flutterwave.raveandroid.rave_remote.responses;

/**
 * Created by hamzafetuga on 26/07/2017.
 */

public class FeeCheckResponse {

    private String message;

    private String status;

    private Data data;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public static class Data {
        private String fee;

        private String charge_amount;

        private String merchantfee;

        private String ravefee;

        public String getFee ()
        {
            return fee;
        }

        public void setFee (String fee)
        {
            this.fee = fee;
        }

        public String getCharge_amount ()
        {
            return charge_amount;
        }

        public void setCharge_amount (String charge_amount)
        {
            this.charge_amount = charge_amount;
        }

        public String getMerchantfee ()
        {
            return merchantfee;
        }

        public void setMerchantfee (String merchantfee)
        {
            this.merchantfee = merchantfee;
        }

        public String getRavefee ()
        {
            return ravefee;
        }

        public void setRavefee (String ravefee)
        {
            this.ravefee = ravefee;
        }
    }
}
