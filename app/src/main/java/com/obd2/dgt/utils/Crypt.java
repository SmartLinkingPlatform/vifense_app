package com.obd2.dgt.utils;


import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    static String ALGORITHM = "AES-256-CBC";
    static String encryptionKey = "dgtsplatformcypt";
    static String iv = "dgtsplatformcypt";

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static String encrypt(String text) {
        try {
            Key aesKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] biv = iv.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(biv));
            byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encryptedBytes);
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
            byte[] biv = iv.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(biv));
            byte[] decryptedBytes = cipher.doFinal(hexToBytes(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            Log.d("Exception dv", e.getMessage());
        }
        return "";
    }
}
