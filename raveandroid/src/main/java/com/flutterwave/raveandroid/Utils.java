package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flutterwave.raveandroid.responses.SubAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Utils {

    private static final String ALGORITHM = "DESede";
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";
    private static final String TARGET = "FLWSECK-";
    private static final String MD5 = "MD5";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String UTF_8 = "utf-8";

    public static String getDeviceImei(Context c) {

        TelephonyManager mTelephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String ip = mTelephonyManager.getDeviceId();

        if (ip == null) {
            ip = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return ip;
    }

    public static boolean wasTxSuccessful(RavePayInitializer ravePayInitializer, String responseAsJSONString){

        String amount = ravePayInitializer.getAmount() + "";
        String currency = ravePayInitializer.getCurrency();

        try {
            JSONObject jsonObject = new JSONObject(responseAsJSONString);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            String status = jsonData.getString("status");
            String txAmount = jsonData.getString("amount");
            String txCurrency = jsonData.getString("currency");
            String chargeResponse = jsonData.getString("chargeResponseCode");

            if (areAmountsSame(amount, txAmount) &&
                    chargeResponse.equalsIgnoreCase("00") &&
                    (status.contains("success") |
                     status.contains("pending-capture")) &&
                    currency.equalsIgnoreCase(txCurrency)) {
                Log.d("RAVE TX V", "true");
                return true;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("RAVE TX V", "false");
            return false;
        }

        return false;
    }

    private static Boolean areAmountsSame(String amount1, String amount2) {
        Double number1 = Double.parseDouble(amount1);
        Double number2 = Double.parseDouble(amount2);

        return Math.abs(number1 - number2) < 0.0001;
    }

    public static String unNullify(String text) {

        if (text == null) {
            return "";
        }

        return text;

    }

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String convertChargeRequestPayloadToJson(Payload body) {

        Gson gson = new Gson();
        Type type = new TypeToken<Payload>() {}.getType();
        return gson.toJson(body, type);
    }

    public static List<Meta> pojofyMetaString(String meta) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Meta>>() {
            }.getType();
            return gson.fromJson(meta, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<SubAccount> pojofySubaccountString(String subaccount) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<SubAccount>>() {
            }.getType();
            return gson.fromJson(subaccount, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String stringifyMeta(List<Meta> meta) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Meta>>() {}.getType();
        return gson.toJson(meta, type);
    }

    public static String stringifySubaccounts(List<SubAccount> subAccounts) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<SubAccount>>() {}.getType();
        return gson.toJson(subAccounts, type);
    }

    public static byte[] RSAEncrypt(String plaintext){
        PublicKey key = getKey("baA/RgjURU3I0uqH3iRos3NbE8fT+lP8SDXKymsnfdPrMQAEoMBuXtoaQiJ1i5tuBG9EgSEOH1LAZEaAsvwClw==");
        byte[] ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = cipher.doFinal(plaintext.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    public static PublicKey getKey(String key){
        try{
            byte[] byteKey = Base64.decode(key.getBytes(Charset.forName("UTF-16")), Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String getEncryptedData(String unEncryptedString, String encryptionKey) {
        try {
            return encrypt(unEncryptedString, encryptionKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptRef(String key, String ref) {
        try {
            return AESCrypt.encrypt(key, ref);
        }catch (GeneralSecurityException e){
            return null;
        }
    }

    public static String decryptRef(String key, String encryptedRef) {
        try {
            return AESCrypt.decrypt(key, encryptedRef);
        }catch (GeneralSecurityException e){
            return null;
        }
    }

    private static String encrypt(String data, String key) throws Exception {
        byte[] keyBytes = key.getBytes(UTF_8);
        SecretKeySpec skey = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(Cipher.ENCRYPT_MODE, skey);
        byte[] plainTextBytes = data.getBytes(UTF_8);
        byte[] buf = cipher.doFinal(plainTextBytes);
        return Base64.encodeToString(buf, Base64.DEFAULT);

    }

    private static String getMd5(String md5) throws Exception {
        MessageDigest md = MessageDigest.getInstance(MD5);
        byte[] array = md.digest(md5.getBytes(CHARSET_NAME));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String obfuscateCardNumber(String first6, String last4) {

        int cardNoLength = first6.length() + last4.length();
        if (cardNoLength < 10) {
            return first6 + last4;
        }
        else {

            int othersLength = 6;

            String exes = "";
            for (int i = 0; i < othersLength; i++) {
                exes += "X";
            }
            return first6 + exes + last4;
        }
    }

    public static String spacifyCardNumber(String cardNo) {

        cardNo = cardNo.replaceAll("\\s", "");
        String spacified = "";

        int len = cardNo.length();

        int nChunks = len/4;
        int rem = len%4;


        for (int i = 0; i < nChunks; i++) {
            spacified += cardNo.substring(i * 4, (i * 4) + 4) + " ";
        }


        spacified += cardNo.substring(nChunks * 4, (nChunks * 4) + rem);

        return spacified;

    }
}
