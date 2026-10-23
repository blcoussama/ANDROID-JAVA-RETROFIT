package com.cabinet.cabinetmedical.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class SessionManagerTest {

    /** Chiffrement factice réversible : l'essentiel est que le texte stocké ne soit pas le clair. */
    private static final class FakeCipher implements SessionCipher {
        boolean failOnEncrypt;

        @Override
        public String encrypt(String plainText) throws GeneralSecurityException {
            if (failOnEncrypt) {
                throw new GeneralSecurityException("Keystore indisponible");
            }
            byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] ^= 0x5A;
            }
            return "v1:" + Base64.getEncoder().encodeToString(bytes);
        }

        @Override
        public String decrypt(String cipherText) throws GeneralSecurityException {
            if (!cipherText.startsWith("v1:")) {
                throw new GeneralSecurityException("Session illisible");
            }
            byte[] bytes = Base64.getDecoder().decode(cipherText.substring(3));
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] ^= 0x5A;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private static final String TOKEN = "jeton-factice-pour-les-tests";

    private InMemorySharedPreferences prefs;
    private FakeCipher cipher;
    private SessionManager manager;

    @Before
    public void setUp() {
        prefs = new InMemorySharedPreferences();
        cipher = new FakeCipher();
        manager = new SessionManager(prefs, cipher);
    }

    private Session patientSession() {
        return new Session(TOKEN, 7L, "patient@test.local", "Nadia", "Test", "PATIENT", 3L, null);
    }

    @Test
    public void savesAndReloadsSession() {
        manager.save(patientSession());

        SessionManager reloaded = new SessionManager(prefs, cipher);
        Session session = reloaded.getSession();
        assertEquals(TOKEN, session.getToken());
        assertEquals("PATIENT", session.getRole());
        assertEquals(Long.valueOf(3L), session.getPatientId());
        assertNull(session.getDoctorId());
        assertTrue(reloaded.isLoggedIn());
    }

    @Test
    public void neverStoresTokenOrEmailInClear() {
        manager.save(patientSession());
        String stored = prefs.getString(SessionManager.KEY_DATA, null);
        assertFalse(stored.contains(TOKEN));
        assertFalse(stored.contains("patient@test.local"));
        assertFalse(stored.contains("PATIENT"));
    }

    @Test
    public void clearRemovesEverything() {
        manager.save(patientSession());
        manager.clear();
        assertNull(manager.getToken());
        assertFalse(manager.isLoggedIn());
        assertTrue(prefs.values.isEmpty());
    }

    @Test
    public void unreadableSessionIsDiscarded() {
        prefs.edit().putString(SessionManager.KEY_DATA, "données corrompues").apply();
        assertNull(manager.getSession());
        assertFalse(prefs.contains(SessionManager.KEY_DATA));
    }

    @Test
    public void nothingIsStoredIfEncryptionFails() {
        cipher.failOnEncrypt = true;
        manager.save(patientSession());
        assertFalse(prefs.contains(SessionManager.KEY_DATA));
        assertNull(manager.getToken());
    }
}
