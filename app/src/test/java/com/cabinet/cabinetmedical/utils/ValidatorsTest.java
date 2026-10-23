package com.cabinet.cabinetmedical.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorsTest {

    @Test
    public void passwordRuleMatchesApi() {
        assertTrue(Validators.isValidPassword("Motdepasse1"));
        assertTrue(Validators.isValidPassword("phrase de passe 2026"));
        assertFalse(Validators.isValidPassword("abc123"));
        assertFalse(Validators.isValidPassword("motdepasse"));
        assertFalse(Validators.isValidPassword("12345678"));
        assertFalse(Validators.isValidPassword(null));
        assertFalse(Validators.isValidPassword(new String(new char[37]).replace("\0", "é1")));
    }

    @Test
    public void emailAndPhone() {
        assertTrue(Validators.isValidEmail("nadia@test.local"));
        assertFalse(Validators.isValidEmail("nadia@"));
        assertFalse(Validators.isValidEmail("na dia@test.local"));
        assertTrue(Validators.isValidOptionalPhone(""));
        assertTrue(Validators.isValidOptionalPhone("0612345678"));
        assertFalse(Validators.isValidOptionalPhone("06123"));
    }
}
