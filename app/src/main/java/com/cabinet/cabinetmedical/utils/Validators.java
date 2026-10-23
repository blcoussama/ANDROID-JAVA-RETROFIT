package com.cabinet.cabinetmedical.utils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/** Règles de saisie alignées sur celles de l'API (l'API reste la référence). */
public final class Validators {

    public static final String PASSWORD_RULE =
            "Au moins 8 caractères, dont une lettre et un chiffre";

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_BYTES = 72;

    private Validators() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 255 && EMAIL.matcher(email).matches();
    }

    /** 8 caractères minimum, au moins une lettre et un chiffre, 72 octets maximum (BCrypt). */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAX_BYTES) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            hasLetter |= Character.isLetter(c);
            hasDigit |= Character.isDigit(c);
        }
        return hasLetter && hasDigit;
    }

    /** Téléphone facultatif ; s'il est saisi : 10 chiffres. */
    public static boolean isValidOptionalPhone(String phone) {
        return phone == null || phone.isEmpty() || PHONE.matcher(phone).matches();
    }
}
