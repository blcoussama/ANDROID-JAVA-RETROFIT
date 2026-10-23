package com.cabinet.cabinetmedical.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.cabinet.cabinetmedical.BuildConfig;
import com.cabinet.cabinetmedical.session.SessionManager;
import com.cabinet.cabinetmedical.ui.auth.LoginActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Retrofit unique de l'application.
 *
 * <p>L'URL vient de {@code BuildConfig.BASE_URL} (définie par type de build dans
 * app/build.gradle.kts). Le client ne retient que le contexte applicatif, jamais une
 * Activity. Les journaux HTTP n'existent qu'en debug, sans corps (mots de passe, motifs
 * de consultation) et avec l'en-tête Authorization masqué.</p>
 */
public final class ApiClient {

    private static final long SESSION_EXPIRED_DEBOUNCE_MS = 3_000;

    private static volatile Retrofit retrofit;
    private static volatile ApiService service;
    private static long lastSessionExpiredAt;

    private ApiClient() {
    }

    public static ApiService getService(Context context) {
        if (service == null) {
            synchronized (ApiClient.class) {
                if (service == null) {
                    service = getClient(context).create(ApiService.class);
                }
            }
        }
        return service;
    }

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BuildConfig.BASE_URL)
                            .client(buildHttpClient(context.getApplicationContext()))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient buildHttpClient(Context appContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(SessionManager.get(appContext),
                        () -> onSessionExpired(appContext)));
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(debugLogging());
        }
        return builder.build();
    }

    /** Jeton expiré ou révoqué : retour à l'écran de connexion (une seule fois si plusieurs requêtes échouent). */
    private static void onSessionExpired(Context appContext) {
        synchronized (ApiClient.class) {
            long now = SystemClock.elapsedRealtime();
            if (now - lastSessionExpiredAt < SESSION_EXPIRED_DEBOUNCE_MS) {
                return;
            }
            lastSessionExpiredAt = now;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(appContext, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_SESSION_EXPIRED, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        });
    }

    /** Journal HTTP de debug : méthode, URL, code et en-têtes, jeton masqué, aucun corps. */
    static HttpLoggingInterceptor debugLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        logging.redactHeader("Authorization");
        logging.redactHeader("Cookie");
        logging.redactHeader("Set-Cookie");
        return logging;
    }
}
