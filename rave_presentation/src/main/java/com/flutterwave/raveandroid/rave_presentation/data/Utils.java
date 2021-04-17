package com.flutterwave.raveandroid.rave_presentation.data;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scottyab.aescrypt.AESCrypt;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.Cipher;

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

    public static String getDeviceId(Context c) {
        return Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
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
        Type type = new TypeToken<Payload>() {
        }.getType();
        String jsonString = gson.toJson(body, type);
        Log.d("Charge parameters",jsonString);
        return jsonString;
    }

    public static List<Meta> pojofyMetaString(String meta) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Meta>>() {
            }.getType();
            return gson.fromJson(meta, type);
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String stringifyMeta(List<Meta> meta) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Meta>>() {
        }.getType();
        return gson.toJson(meta, type);
    }

    public static String stringifySubaccounts(List<SubAccount> subAccounts) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<SubAccount>>() {
        }.getType();
        return gson.toJson(subAccounts, type);
    }

    public static byte[] RSAEncrypt(String plaintext) {
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

    public static PublicKey getKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key.getBytes(Charset.forName("UTF-16")), Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encryptRef(String key, String ref) {
        try {
            return AESCrypt.encrypt(key, ref);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    public static String decryptRef(String key, String encryptedRef) {
        try {
            return AESCrypt.decrypt(key, encryptedRef);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    private static String getMd5(String md5) throws Exception {
        MessageDigest md = MessageDigest.getInstance(MD5);
        byte[] array = md.digest(md5.getBytes(CHARSET_NAME));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }

    public static String obfuscateCardNumber(String first6, String last4) {

        int cardNoLength = first6.length() + last4.length();
        if (cardNoLength < 10) {
            return first6 + last4;
        } else {

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

        int nChunks = len / 4;
        int rem = len % 4;


        for (int i = 0; i < nChunks; i++) {
            spacified += cardNo.substring(i * 4, (i * 4) + 4) + " ";
        }


        spacified += cardNo.substring(nChunks * 4, (nChunks * 4) + rem);

        return spacified;

    }

    /**
     * Checks that a number is valid according to the
     * <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn algorithm</a>
     *
     * @param number to be checked
     * @return true if valid
     */
    public static boolean isValidLuhnNumber(String number) {

        try {// Verify that string contains only numbers
            Long parsed = Long.parseLong(number);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String reversedNumber = new StringBuffer(number).reverse().toString();
        char[] reversedCharArray = reversedNumber.toCharArray();
        int luhnSum = 0;

        // Double the value of every second digit from the right
        for (int index = 1; index < number.length(); index += 2) {
            int doubleResult = Character.getNumericValue(reversedCharArray[index]) * 2;
            if (doubleResult > 9) {// If result has double digits, sum digits
                doubleResult = 1 + (doubleResult - 10);
            }

            reversedCharArray[index] = Character.forDigit(doubleResult, 10);
        }

        // Sum all digits
        for (int index = 0; index < number.length(); index++) {
            luhnSum += Character.getNumericValue(reversedCharArray[index]);
        }

        // For valiid Luhn number, sum result should be a multiple of 10
        return luhnSum % 10 == 0;
    }
}
