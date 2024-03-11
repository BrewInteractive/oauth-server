package com.brew.oauth20.server.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils {
    private static final String AES_CIPHER_SPEC = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final int IV_SIZE = 12; // Recommended IV size for GCM mode
    private static final SecureRandom secureRandom = new SecureRandom();

    private EncryptionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String encrypt(String data, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        var cipher = Cipher.getInstance(AES_CIPHER_SPEC);
        var encryptionKey = secret.getBytes();

        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv); // Generate a new IV for this encryption

        GCMParameterSpec paramSpec = new GCMParameterSpec(128, iv); // 128-bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey, ALGORITHM), paramSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Prepend IV to encrypted data and encode the result as Base64
        byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

        return java.util.Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        var cipher = Cipher.getInstance(AES_CIPHER_SPEC);
        var encryptionKey = secret.getBytes();

        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        GCMParameterSpec paramSpec = new GCMParameterSpec(128, Arrays.copyOfRange(decodedData, 0, IV_SIZE));

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey, ALGORITHM), paramSpec);
        byte[] decryptedDataBytes =
                cipher.doFinal(Arrays.copyOfRange(decodedData, IV_SIZE, decodedData.length));
        return new String(decryptedDataBytes);
    }
}
