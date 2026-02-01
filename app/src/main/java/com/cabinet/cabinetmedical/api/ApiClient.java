package com.cabinet.cabinetmedical.api;

import android.content.Context;

import com.cabinet.cabinetmedical.utils.JwtInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient - Configuration Retrofit pour les appels API

 * RESPONSABILITÉS:
 * - Créer et configurer l'instance Retrofit
 * - Configurer OkHttp (timeouts, intercepteurs)
 * - Ajouter JwtInterceptor (token JWT automatique)
 * - Ajouter LoggingInterceptor (debug requêtes)
 * - Convertisseur Gson (JSON ↔ Java objects)

 * UTILISATION:
 * ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
 * Call<LoginResponse> call = apiService.login(request);

 * CONFIGURATION:
 * - BASE_URL: http://172.25.135.62:8080/ (IP WSL2 + port Spring Boot)
 * - Timeouts: 30 secondes
 * - Logging: BODY (voir toutes les requêtes/réponses)
 * - JWT: Automatique via JwtInterceptor

 * NOTES IMPORTANTES:
 * - BASE_URL doit se terminer par "/"
 * - L'IP WSL2 peut changer au redémarrage Windows
 * - En production: utiliser HTTPS + nom de domaine
 * - Singleton pattern: une seule instance Retrofit
 */
public class ApiClient {

    /**
     * URL de base de l'API backend
     *
     * DÉVELOPPEMENT (WSL2):
     * - Format: http://[IP_WSL2]:8080/
     * - Exemple: http://172.25.135.62:8080/
     * - L'IP peut changer (voir dans WSL2: ip addr show eth0)
     *
     * PRODUCTION:
     * - Format: https://api.cabinet-medical.com/
     * - HTTPS obligatoire en production !
     *
     * IMPORTANT:
     * - DOIT se terminer par "/"
     * - Endpoints ApiService n'ont PAS de "/" au début
     * - Exemple endpoint: "api/auth/login" (pas "/api/auth/login")
     *
     * COMMENT TROUVER L'IP WSL2:
     * Dans terminal WSL2: ip addr show eth0 | grep inet
     * Résultat: inet 172.25.135.62/20
     */
    private static final String BASE_URL = "https://android-mobile-java-springboot-app-backend-production.up.railway.app/";

    /**
     * Instance Retrofit (singleton)
     */
    private static Retrofit retrofit = null;

    /**
     * Obtenir l'instance Retrofit configurée
     *
     * PATTERN SINGLETON:
     * - Une seule instance créée
     * - Réutilisée pour toutes les requêtes
     * - Optimise les performances
     *
     * @param context Context Android (pour JwtInterceptor)
     * @return Instance Retrofit configurée
     */
    public static Retrofit getClient(Context context) {
        // Si déjà créé, retourner l'instance existante
        if (retrofit != null) {
            return retrofit;
        }

        // Créer le logging interceptor (pour debug)
        // Affiche les requêtes/réponses dans Logcat
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Créer le client OkHttp avec intercepteurs
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new JwtInterceptor(context)) // JWT automatique
                .addInterceptor(loggingInterceptor)          // Logging debug
                .build();

        // Créer l'instance Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)                                    // URL de base
                .client(okHttpClient)                                 // Client OkHttp configuré
                .addConverterFactory(GsonConverterFactory.create())   // Conversion JSON ↔ Java
                .build();

        return retrofit;
    }

    /**
     * Réinitialiser l'instance Retrofit

     * UTILISATION:
     * - Changer d'URL (dev → prod)
     * - Forcer recréation (changement config)
     * - Tests unitaires

     * Après reset(), le prochain getClient() créera une nouvelle instance
     */
    public static void reset() {
        retrofit = null;
    }
}