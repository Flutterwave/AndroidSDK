package com.flutterwave.raveandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flutterwave.raveandroid.responses.SubAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scottyab.aescrypt.AESCrypt;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Utils {

    private static final String ALGORITHM = "DESede";
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

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
        return gson.toJson(body, type);
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

    public static String getEncryptedData(String unEncryptedString, String encryptionKey) {

        if (unEncryptedString != null && encryptionKey != null) {
            try {
                return encrypt(unEncryptedString, encryptionKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
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

    private static String encrypt(String data, String key) throws Exception {

        try {
            @SuppressLint({"NewApi", "LocalSuppress"}) byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE, skey);
            @SuppressLint({"NewApi", "LocalSuppress"}) byte[] plainTextBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] buf = cipher.doFinal(plainTextBytes);
            return Base64.encodeToString(buf, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }

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
     * Checks that a number is valid according to the Luhn algorithm
     * https://en.wikipedia.org/wiki/Luhn_algorithm
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
