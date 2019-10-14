package com.flutterwave.raveandroid;

/**
 * Created by hamzafetuga on 14/07/2017.
 */

public class RaveConstants {
    public static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 419;

    //    public static String PUBLIC_KEY = "FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X"; //test
    public static String PUBLIC_KEY = "FLWPUBK-699784a2d0d432e9da429606764b6e28-X"; //live
    //    public static String ENCRYPTION_KEY = "bb9714020722eb4cf7a169f2";//test
    public static String ENCRYPTION_KEY = "f26727fddb9dd748e5073e73";//live
    //    public static String STAGING_URL = "https://ravesandbox.azurewebsites.net";
//    public static String LIVE_URL = "https://raveapi.azurewebsites.net";
    //Todo: change back BAse URL
    public static String STAGING_URL = "https://ravesandboxapi.flutterwave.com";
    public static String LIVE_URL = "https://api.ravepay.co";

    public static String VBV = "VBVSECURECODE";
    public static String GTB_OTP = "GTB_OTP";
    public static String NOAUTH = "NOAUTH";
    public static String PIN = "PIN";
    public static String AVS_VBVSECURECODE = "AVS_VBVSECURECODE";
    public static String NOAUTH_INTERNATIONAL = "NOAUTH_INTERNATIONAL";
    public static String RAVEPAY = "ravepay";
    public static String RAVE_PARAMS = "raveparams";
    public static String RAVE_3DS_CALLBACK = "https://rave-webhook.herokuapp.com/receivepayment";
    public static int RAVE_REQUEST_CODE = 4199;
    public static int MANUAL_CARD_CHARGE = 403;
    public static int TOKEN_CHARGE = 24;
}
