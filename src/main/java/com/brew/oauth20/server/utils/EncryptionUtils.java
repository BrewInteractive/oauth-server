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
import java.util.HashMap;
import java.util.Map;

public class EncryptionUtils {
    private static final String AES_CIPHER_SPEC_KEY = "AES_CIPHER_SPEC";
    private static final String ALGORITHM_KEY = "ALGORITHM";
    private static final int IV_SIZE = 12; // Recommended IV size for GCM mode
    private static final SecureRandom secureRandom = new SecureRandom();

    private EncryptionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, String> createAlgorithmKeyHashmap(String algorithm, String cipherSpec) {
        var algorithms = new HashMap<String, String>();
        algorithms.put(AES_CIPHER_SPEC_KEY, cipherSpec);
        algorithms.put(ALGORITHM_KEY, algorithm);
        return algorithms;
    }

    public static String encrypt(String data, Map<String, String> algorithms, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        var cipher = Cipher.getInstance(algorithms.get(AES_CIPHER_SPEC_KEY));
        var encryptionKey = secret.getBytes();

        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv); // Generate a new IV for this encryption

        GCMParameterSpec paramSpec = new GCMParameterSpec(128, iv); // 128-bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey, algorithms.get(ALGORITHM_KEY)), paramSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Prepend IV to encrypted data and encode the result as Base64
        byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

        return java.util.Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, Map<String, String> algorithms, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        var cipher = Cipher.getInstance(algorithms.get(AES_CIPHER_SPEC_KEY));
        var encryptionKey = secret.getBytes();

        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        GCMParameterSpec paramSpec = new GCMParameterSpec(128, Arrays.copyOfRange(decodedData, 0, IV_SIZE));

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey, algorithms.get(ALGORITHM_KEY)), paramSpec);
        byte[] decryptedDataBytes =
                cipher.doFinal(Arrays.copyOfRange(decodedData, IV_SIZE, decodedData.length));
        return new String(decryptedDataBytes);
    }
}
