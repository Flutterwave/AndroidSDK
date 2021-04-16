package com.flutterwave.raveandroid.rave_java_commons;

import com.flutterwave.raveandroid.rave_core.models.Bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hamzafetuga on 14/07/2017.
 */

public class RaveConstants {
    public static final int SAVED_CARD_CHARGE = 5699;

    public static final int RESULT_SUCCESS = 111;
    public static final int RESULT_ERROR = 222;
    public static final int RESULT_CANCELLED = 333;

    public static final int RAVE_REQUEST_CODE = 4199;
    public static final int OTP_REQUEST_CODE = 5399;
    public static final int WEB_VERIFICATION_REQUEST_CODE = 5340;
    public static final int BARTER_CHECKOUT_REQUEST_CODE = 5341;
    public static final int PIN_REQUEST_CODE = 5342;
    public static final int ADDRESS_DETAILS_REQUEST_CODE = 5343;
    public static final int MANUAL_CARD_CHARGE = 403;

    public static String PUBLIC_KEY = "FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X"; //test
    public static String ENCRYPTION_KEY = "bb9714020722eb4cf7a169f2";//test
    //    public static String STAGING_URL = "https://ravesandbox.azurewebsites.net";
//    public static String LIVE_URL = "https://raveapi.azurewebsites.net";
    public static String STAGING_URL = "https://ravesandboxapi.flutterwave.com";
    public static String LIVE_URL = "https://api.ravepay.co";
    public static String CARD_CHECK_URL = "https://9wd5x7szl1.execute-api.eu-west-2.amazonaws.com";
    public static String EVENT_LOGGING_URL = "https://kgelfdz7mf.execute-api.us-east-1.amazonaws.com/";
    public static String FLUTTERWAVE_UK_ACCOUNT = "43271228";
    public static String FLUTTERWAVE_UK_SORT_CODE = "04-00-53";
    public static String FLUTTERWAVE_UK_BENEFICIARY_NAME = "Barter Funding";


    public static String VBV = "VBVSECURECODE";
    public static String GTB_OTP = "GTB_OTP";
    public static String ACCESS_OTP = "ACCESS_OTP";
    public static String NG = "NG";
    public static String NGN = "NGN";
    public static String UGX = "UGX";
    public static String RWF = "RWF";
    public static String NOAUTH = "NOAUTH";
    public static String NOAUTH_SAVED_CARD = "noauth-saved-card";
    public static String PIN = "PIN";
    public static String selectNetwork = "Select network";
    public static String AVS_VBVSECURECODE = "AVS_VBVSECURECODE";
    public static String enterOTP = "Enter your one time password (OTP)";
    public static String NOAUTH_INTERNATIONAL = "NOAUTH_INTERNATIONAL";
    public static String RAVEPAY = "ravepay";
    public static String RAVE_PARAMS = "raveparams";
    public static String RAVE_3DS_CALLBACK = "https://rave-webhook.herokuapp.com/receivepayment";
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
    public static String cardNotAllowed = "You can’t fund with cards from other countries yet. Please add a card issued in your country to proceed.";
    public static String invalidAccountNoMessage = "Enter a valid account number";
    public static String invalidDateOfBirthMessage = "Enter a valid date of birth";
    public static String invalidBvnMessage = "Enter a valid BVN";
    public static String invalidBankCodeMessage = "You need to select bank";
    public static String defaultAccounNumber = "0000000000";
    public static String inValidRedirectUrl = "Invalid redirect url returned";

    public static String response = "response";
    public static String mtn = "MTN";
    public static String tigo = "TIGO";
    public static String vodafone = "VODAFONE";

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
    public static String validCreditCardPrompt = "Enter a valid card number";
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

    /**
     * The list of banks for USSD charge. This list should match that in [R.string.ussd_banks]
     */
    public static ArrayList<Bank> ussdBanksList = new ArrayList<>(Arrays.asList(
            new Bank("Access Bank", "044"),
            new Bank("EcoBank", "050"),
            new Bank("Fidelity Bank",  "070"),
            new Bank("First Bank of Nigeria", "011"),
            new Bank("First City Monument Bank", "214"),
            new Bank(bankNameGtb,  "058"),
            new Bank("Heritage Bank", "030"),
            new Bank("Keystone Bank",  "082"),
            new Bank("Stanbic IBTC Bank", "221"),
            new Bank("Sterling Bank PLC",  "232"),
            new Bank("Union Bank", "032"),
            new Bank("United Bank for Africa", "033"),
            new Bank("Unity Bank PLC",  "215"),
            new Bank("VFD Microfinance Bank", "090110"),
            new Bank("Wema Bank", "035"),
            new Bank("Zenith bank PLC", "057")
    ));
    public static List<Bank> accountBanksList = new ArrayList<>(Arrays.asList(
            new Bank(bankNameGtb, "058"),
            new Bank("FIRST BANK PLC", "011")
    ));

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


    public static final String responseParsingError = "Error parsing server response";
    public static String errorParsingError = "An error occurred parsing the error response";
    public static String eTransact_GH = "ETRANZACT_GH";
}
