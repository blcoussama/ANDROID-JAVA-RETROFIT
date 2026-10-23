package com.cabinet.cabinetmedical.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.cabinet.cabinetmedical.model.response.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.security.GeneralSecurityException;

/**
 * Session de l'utilisateur : jeton JWT et profil, sérialisés en JSON puis chiffrés
 * (AES-GCM, clé de l'Android Keystore) avant d'être écrits dans les SharedPreferences.
 * Rien n'est stocké en clair. Une session illisible (clé perdue, données corrompues)
 * est supprimée : l'utilisateur se reconnecte.
 */
public final class SessionManager implements TokenStore {

    static final String PREFS_NAME = "session";
    static final String KEY_DATA = "data";
    /** Ancien fichier de préférences en clair (versions précédentes), supprimé au démarrage. */
    private static final String LEGACY_PREFS_NAME = "auth";

    private static volatile SessionManager instance;

    private final SharedPreferences prefs;
    private final SessionCipher cipher;
    private final Gson gson = new Gson();
    private Session cached;
    private boolean loaded;

    SessionManager(SharedPreferences prefs, SessionCipher cipher) {
        this.prefs = prefs;
        this.cipher = cipher;
    }

    public static SessionManager get(Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    Context app = context.getApplicationContext();
                    app.deleteSharedPreferences(LEGACY_PREFS_NAME);
                    instance = new SessionManager(app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE),
                            new KeystoreSessionCipher());
                }
            }
        }
        return instance;
    }

    public synchronized void save(LoginResponse response) {
        save(Session.from(response));
    }

    public synchronized void save(Session session) {
        try {
            String encrypted = cipher.encrypt(gson.toJson(session));
            prefs.edit().putString(KEY_DATA, encrypted).apply();
            cached = session;
            loaded = true;
        } catch (GeneralSecurityException e) {
            // Impossible de chiffrer : on ne stocke rien plutôt que de stocker en clair
            clear();
        }
    }

    /** Session courante, ou null si personne n'est connecté. */
    public synchronized Session getSession() {
        if (!loaded) {
            cached = load();
            loaded = true;
        }
        return cached;
    }

    @Override
    public synchronized String getToken() {
        Session session = getSession();
        return session != null ? session.getToken() : null;
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    @Override
    public synchronized void clear() {
        prefs.edit().remove(KEY_DATA).apply();
        cached = null;
        loaded = true;
    }

    private Session load() {
        String stored = prefs.getString(KEY_DATA, null);
        if (stored == null) {
            return null;
        }
        try {
            return gson.fromJson(cipher.decrypt(stored), Session.class);
        } catch (GeneralSecurityException | JsonParseException e) {
            prefs.edit().remove(KEY_DATA).apply();
            return null;
        }
    }
}
