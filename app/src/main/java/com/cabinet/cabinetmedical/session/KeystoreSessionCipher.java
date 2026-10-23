package com.cabinet.cabinetmedical.session;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * AES-256-GCM avec une clé générée et conservée dans l'Android Keystore : la clé ne
 * quitte jamais le Keystore et n'est pas exportable. Format stocké : Base64(IV || chiffré).
 */
public final class KeystoreSessionCipher implements SessionCipher {

    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "cabinet_medical_session";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    @Override
    public String encrypt(String plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey());
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
        buffer.put(iv).put(encrypted);
        return Base64.encodeToString(buffer.array(), Base64.NO_WRAP);
    }

    @Override
    public String decrypt(String cipherText) throws GeneralSecurityException {
        byte[] data;
        try {
            data = Base64.decode(cipherText, Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException("Session illisible", e);
        }
        if (data.length <= IV_LENGTH) {
            throw new GeneralSecurityException("Session illisible");
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), new GCMParameterSpec(TAG_LENGTH_BITS, data, 0, IV_LENGTH));
        byte[] plain = cipher.doFinal(data, IV_LENGTH, data.length - IV_LENGTH);
        return new String(plain, StandardCharsets.UTF_8);
    }

    private static SecretKey getOrCreateKey() throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE);
            keyStore.load(null);
            if (keyStore.getKey(KEY_ALIAS, null) instanceof SecretKey existing) {
                return existing;
            }
        } catch (java.io.IOException e) {
            throw new GeneralSecurityException("Keystore indisponible", e);
        }
        KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
        generator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build());
        return generator.generateKey();
    }
}
