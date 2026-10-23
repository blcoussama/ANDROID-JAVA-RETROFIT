package com.cabinet.cabinetmedical.session;

import java.security.GeneralSecurityException;

/** Chiffrement de la session au repos. */
public interface SessionCipher {

    String encrypt(String plainText) throws GeneralSecurityException;

    String decrypt(String cipherText) throws GeneralSecurityException;
}
