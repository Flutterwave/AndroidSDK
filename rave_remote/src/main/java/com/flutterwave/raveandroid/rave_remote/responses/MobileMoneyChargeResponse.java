package com.flutterwave.raveandroid.rave_remote.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class MobileMoneyChargeResponse {
    String status;
    String message;
    Data data;

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

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        String suggested_auth;
        String chargeResponseCode;
        String authModelUsed;
        String flwRef;
        String txRef;
        String chargeResponseMessage;
        String authurl;
        String redirectUrl;
        @SerializedName("link")
        String captchaLink;
        String code;
        @Nullable
        String provider;
        String validateInstructions;

        @Nullable
        public String getProvider() {
            return provider;
        }

        public void setProvider(@Nullable String provider) {
            this.provider = provider;
        }

        public String getValidateInstructions() {
            return validateInstructions;
        }

        public void setValidateInstructions(String validateInstructions) {
            this.validateInstructions = validateInstructions;
        }

        public void setFlwRef(String flwRef) {
            this.flwRef = flwRef;
        }

        public void setAuthurl(String authurl) {
            this.authurl = authurl;
        }

        public String getTx_ref() {
            return txRef;
        }

        public void setTx_ref(String txRef) {
            this.txRef = txRef;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setChargeResponseCode(String chargeResponseCode) {
            this.chargeResponseCode = chargeResponseCode;
        }


        public String getAuthurl() {
            return authurl;
        }

        public String getFlwRef() {
            return flwRef;
        }

        public String getChargeResponseMessage() {
            return chargeResponseMessage;
        }

        public String getAuthModelUsed() {
            return authModelUsed;
        }

        public String getChargeResponseCode() {
            return chargeResponseCode;
        }


        public String getSuggested_auth() {
            return suggested_auth;
        }


        public String getTxRef() {
            return txRef;
        }

        public String getCaptchaLink() {
            return captchaLink;
        }

        public String getCode() {
            return code;
        }

    }
}
