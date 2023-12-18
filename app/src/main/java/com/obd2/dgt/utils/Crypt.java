package com.obd2.dgt.utils;


import android.os.Build;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Crypt {
    static String ALGORITHM = "AES-256-CBC";
    static String encryptionKey = "dgtsplatformcypt";
    static String iv = "dgtsplatformcypt";

    public static String encrypt(String text) {
        try {
            Key aesKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher_v = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher_v.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] encrypted = cipher_v.doFinal(text.getBytes(StandardCharsets.UTF_8));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                return new String(Base64.getEncoder().encode(encrypted));
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            Log.d("Exception ev", e.getMessage());
        }
        return "";
    }

    public static String decrypt(String encryptedText) {
        try {
            Key aesKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] decrypted = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            }
            return new String(decrypted, "UTF-8");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            Log.d("Exception dv", e.getMessage());
        }
        return "";
    }

}
