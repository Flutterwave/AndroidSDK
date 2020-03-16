package com.flutterwave.raveandroid;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PayloadEncryptor {

    private static final String ALGORITHM = "DESede";
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

    @Inject
    public PayloadEncryptor() {
    }

    public String getEncryptedData(String unEncryptedString, String encryptionKey) {

        if (unEncryptedString != null && encryptionKey != null) {
            try {
                return encrypt(unEncryptedString, encryptionKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String encrypt(String data, String key) throws Exception {

        try {
            byte[] keyBytes = key.getBytes(Utils.UTF_8);
            SecretKeySpec skey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE, skey);
            byte[] plainTextBytes = data.getBytes(Utils.UTF_8);
            byte[] buf = cipher.doFinal(plainTextBytes);
            return Base64.encodeToString(buf, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }

    }

}
