package com.flutterwave.raveandroid;

import java.util.HashMap;

/**
 * Created by hamzafetuga on 14/07/2017.
 */

public class RaveConstants {
    public static final int SAVED_CARD_CHARGE = 5699;

    public static String PUBLIC_KEY = "FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X"; //test
    public static String ENCRYPTION_KEY = "bb9714020722eb4cf7a169f2";//test
    //    public static String STAGING_URL = "https://ravesandbox.azurewebsites.net";
//    public static String LIVE_URL = "https://raveapi.azurewebsites.net";
    public static String STAGING_URL = "https://ravesandboxapi.flutterwave.com";
    public static String LIVE_URL = "https://api.ravepay.co";
    public static String EVENT_LOGGING_URL = "https://kgelfdz7mf.execute-api.us-east-1.amazonaws.com/";

    public static String VBV = "VBVSECURECODE";
    public static String GTB_OTP = "GTB_OTP";
    public static String ACCESS_OTP = "ACCESS_OTP";
    public static String NG = "NG";
    public static String NGN = "NGN";
    public static String UGX = "UGX";
    public static String RWF = "RWF";
    public static String NOAUTH = "NOAUTH";
    public static String PIN = "PIN";
    public static String selectNetwork = "Select network";
    public static String AVS_VBVSECURECODE = "AVS_VBVSECURECODE";
    public static String enterOTP = "Enter your one time password (OTP)";
    public static String NOAUTH_INTERNATIONAL = "NOAUTH_INTERNATIONAL";
    public static String RAVEPAY = "ravepay";
    public static String RAVE_PARAMS = "raveparams";
    public static String RAVE_3DS_CALLBACK = "https://rave-webhook.herokuapp.com/receivepayment";
    public static int RAVE_REQUEST_CODE = 4199;
    public static int MANUAL_CARD_CHARGE = 403;
    public static int TOKEN_CHARGE = 24;
    public static String fieldAmount = "amount";
    public static String fieldPhone = "phone";
    public static String fieldAccountName = "accountname";
    public static String fieldAccountBank = "accountbank";
    public static String fieldAccountNumber = "accountnumber";
    public static String fieldEmail = "email";
    public static String fieldAccount = "account";
    public static String fieldVoucher = "voucher";
    public static String fieldNetwork = "network";
    public static String networkPosition = "position";
    public static String fieldBVN = "bvn";
    public static String fieldDOB = "dob";
    public static String fieldBankCode = "bankcode";
    public static String fieldCvv = "cvv";
    public static String fieldCardExpiry = "cardExpiry";
    public static String fieldcardNoStripped = "cardNoStripped";
    public static String fieldUssdBank = "ussdbank";
    public static String date_of_birth = "Date of Birth";
    public static String isInternetBanking = "bankcode";

    public static String success = "success";
    public static String noResponse = "No response data was returned";
    public static String invalidAccountNoMessage = "Enter a valid account number";
    public static String invalidDateOfBirthMessage = "Enter a valid date of birth";
    public static String invalidBvnMessage = "Enter a valid BVN";
    public static String invalidBankCodeMessage = "You need to select bank";
    public static String defaultAccounNumber = "0000000000";
    public static String inValidRedirectUrl = "Invalid redirect url returned";

    public static String response = "response";
    public static String mtn = "mtn";
    public static String tigo = "tigo";
    public static String vodafone = "vodafone";

    public static String tokenNotFound = "token not found";
    public static String expired = "expired";
    public static String tokenExpired = "Token expired";
    public static String cardNoStripped = "cardNoStripped";
    public static String validAmountPrompt = "Enter a valid amount";
    public static String validPhonePrompt = "Enter a valid number";
    public static String validEmailPrompt = "Enter a valid Email";
    public static String validAccountNumberPrompt = "Enter a valid Account Number";
    public static String validAccountNamePrompt = "Enter a valid Account Name";
    public static String validBankNamePrompt = "Enter a valid Bank Name";
    public static String charge = "You will be charged a total of ";
    public static String askToContinue = ". Do you want to continue?";
    public static String yes = "YES";
    public static String no = "NO";
    public static String cancel = "CANCEL";
    public static String checkStatus = "Checking transaction status.  Please wait";
    public static String transactionError = "An error occurred while retrieving transaction fee";
    public static String validCvvPrompt = "Enter a valid cvv";
    public static String validExpiryDatePrompt = "Enter a valid expiry date";
    public static String validCreditCardPrompt = "Enter a valid credit card number";
    public static String validVoucherPrompt = "Enter a valid voucher code";
    public static String validNetworkPrompt = "Select a network";
    public static String invalidChargeCode = "Invalid charge response code";
    public static String invalidCharge = "Invalid charge card response";
    public static String unknownAuthmsg = "Unknown Auth Model";
    public static String unknownResCodemsg = "Unknown charge response code";
    public static String no_authurl_was_returnedmsg = "No authUrl was returned";
    public static String wait = "Please wait...";
    public static String cancelPayment = "CANCEL PAYMENT";
    public static String bankNameGtb = "Guaranty Trust Bank";
    public static final String BARTER_CHECKOUT = "barter";


    public static HashMap<String, String> ussdBanksList = new HashMap<String, String>() {{
        put(bankNameGtb, "058");
        put("Fidelity Bank", "070");
        put("Keystone Bank", "082");
        put("Unity Bank PLC", "215");
        put("Zenith bank PLC", "057");
        put("Sterling Bank PLC", "232");
        put("United Bank for Africa", "033");
    }};

    public static final int PAYMENT_TYPE_CARD = 101;
    public static final int PAYMENT_TYPE_ACCOUNT = 102;
    public static final int PAYMENT_TYPE_GH_MOBILE_MONEY = 103;
    public static final int PAYMENT_TYPE_RW_MOBILE_MONEY = 104;
    public static final int PAYMENT_TYPE_MPESA = 105;
    public static final int PAYMENT_TYPE_UG_MOBILE_MONEY = 106;
    public static final int PAYMENT_TYPE_ACH = 107;
    public static final int PAYMENT_TYPE_ZM_MOBILE_MONEY = 108;
    public static final int PAYMENT_TYPE_BANK_TRANSFER = 109;
    public static final int PAYMENT_TYPE_UK = 110;
    public static final int PAYMENT_TYPE_USSD = 111;
    public static final int PAYMENT_TYPE_FRANCO_MOBILE_MONEY = 112;
    public static final int PAYMENT_TYPE_BARTER = 113;
    public static final int PAYMENT_TYPE_SA_BANK_ACCOUNT = 114;

    public static HashMap<Integer, String> paymentTypesNamesList = new HashMap<Integer, String>() {{
        put(PAYMENT_TYPE_CARD, "Card");
        put(PAYMENT_TYPE_ACCOUNT, "Account");
        put(PAYMENT_TYPE_GH_MOBILE_MONEY, "Ghana Mobile Money");
        put(PAYMENT_TYPE_RW_MOBILE_MONEY, "Rwanda Mobile Money");
        put(PAYMENT_TYPE_UG_MOBILE_MONEY, "Uganda Mobile Money");
        put(PAYMENT_TYPE_ZM_MOBILE_MONEY, "Zambia Mobile Money");
        put(PAYMENT_TYPE_FRANCO_MOBILE_MONEY, "Francophone Mobile Money");
        put(PAYMENT_TYPE_MPESA, "M-Pesa");
        put(PAYMENT_TYPE_ACH, "ACH");
        put(PAYMENT_TYPE_BANK_TRANSFER, "Bank Transfer");
        put(PAYMENT_TYPE_UK, "UK Bank Account");
        put(PAYMENT_TYPE_BARTER, "Barter");
        put(PAYMENT_TYPE_USSD, "USSD");
        put(PAYMENT_TYPE_SA_BANK_ACCOUNT, "South Africa Bank Account");
    }};


}
