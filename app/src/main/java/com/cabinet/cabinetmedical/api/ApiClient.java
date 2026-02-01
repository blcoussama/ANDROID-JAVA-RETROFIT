package com.cabinet.cabinetmedical.api;

import android.content.Context;

import com.cabinet.cabinetmedical.utils.JwtInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Configuration Retrofit pour les appels API REST
 *
 * Singleton pattern avec configuration automatique selon le build type:
 * - DEBUG: Backend local (http://IP_WSL:8080/)
 * - RELEASE: Backend production (Railway HTTPS)
 *
 * Configuration:
 * - JwtInterceptor: Ajoute automatiquement le token JWT aux requêtes
 * - LoggingInterceptor: Log les requêtes/réponses (debug uniquement)
 * - GsonConverter: Conversion automatique JSON ↔ Java
 */
public class ApiClient {

    /**
     * URL de base de l'API backend
     *
     * DÉVELOPPEMENT LOCAL: Décommenter la ligne ci-dessous
     * private static final String BASE_URL = "http://172.25.135.62:8080/";
     *
     * PRODUCTION RAILWAY: Décommenter la ligne ci-dessous (ACTIVE)
     */
    private static final String BASE_URL = "https://android-mobile-java-springboot-app-backend-production.up.railway.app/";

    private static Retrofit retrofit = null;

    /**
     * Obtenir l'instance Retrofit (singleton)
     *
     * @param context Context Android pour JwtInterceptor
     * @return Instance Retrofit configurée
     */
    public static Retrofit getClient(Context context) {
        if (retrofit != null) {
            return retrofit;
        }

        // Log URL utilisée
        android.util.Log.i("ApiClient", "🔗 BASE_URL: " + BASE_URL);

        // Logging interceptor (debug)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Client OkHttp avec intercepteurs
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .build();

        // Instance Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    /**
     * Réinitialiser l'instance Retrofit
     * Utile pour les tests ou changement de configuration
     */
    public static void reset() {
        retrofit = null;
    }
}
