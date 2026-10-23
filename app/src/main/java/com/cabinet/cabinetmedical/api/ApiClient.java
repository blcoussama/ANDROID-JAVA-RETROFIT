package com.cabinet.cabinetmedical.api;

import android.content.Context;

import com.cabinet.cabinetmedical.BuildConfig;
import com.cabinet.cabinetmedical.utils.JwtInterceptor;

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

    private static volatile Retrofit retrofit;

    private ApiClient() {
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

    public static ApiService getService(Context context) {
        return getClient(context).create(ApiService.class);
    }

    private static OkHttpClient buildHttpClient(Context appContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new JwtInterceptor(appContext));
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(debugLogging());
        }
        return builder.build();
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
