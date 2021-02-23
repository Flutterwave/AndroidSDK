package com.flutterwave.raveandroid.rave_remote;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

    public static String sha(String input, String type) {
        return hashString(type, input);
    }

    private static String hashString(String type, String input) {

        try {
//            byte[] b = input.getBytes();
//            String HEX_CHARS = "0123456789ABCDEF";
//            byte[] bytes = MessageDigest.getInstance(type).digest(b);
//
//            StringBuilder result = new StringBuilder(bytes.length * 2);
//
//            for (int i = 0; i < bytes.length; i++) {
//
//                int eachByte = (int) i;
//                result.append(HEX_CHARS.charAt(eachByte >> 4 & 0x0f));
//                result.append(HEX_CHARS.charAt(eachByte & 0x0f));
//            }

//            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes("UTF-8"));
                StringBuffer hexString = new StringBuffer();

                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                return hexString.toString();
            } catch(Exception ex){
                return "";
            }
//            return result.toString();
//        } catch (NoSuchAlgorithmException exception) {
//            return "";
//        }

    }
}
