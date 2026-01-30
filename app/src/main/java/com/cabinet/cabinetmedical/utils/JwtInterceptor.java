package com.cabinet.cabinetmedical.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * JwtInterceptor - Intercepteur OkHttp pour ajouter le token JWT

 * RESPONSABILITÉS:
 * - Intercepter TOUTES les requêtes HTTP sortantes
 * - Récupérer le token JWT depuis SharedPreferences
 * - Ajouter le header "Authorization: Bearer {token}"
 * - Laisser passer la requête

 * FLOW:
 * 1. Retrofit prépare une requête (ex: GET /api/appointments/patient/1)
 * 2. OkHttp passe par l'intercepteur AVANT d'envoyer
 * 3. JwtInterceptor ajoute le header Authorization
 * 4. Requête envoyée avec token au backend
 * 5. Backend valide le token avec JwtAuthenticationFilter
 *
 * EXEMPLE SANS INTERCEPTEUR:
 * GET /api/appointments/patient/1
 * Headers: (aucun)
 * → Backend: 401 Unauthorized ❌
 *
 * EXEMPLE AVEC INTERCEPTEUR:
 * GET /api/appointments/patient/1
 * Headers: Authorization: Bearer eyJhbGc...
 * → Backend: Token valide ✅ → 200 OK ✅
 *
 * STOCKAGE TOKEN:
 * SharedPreferences "auth"
 * └── jwt_token: "eyJhbGciOiJIUzI1NiJ9..."
 * └── user_id: 1
 * └── user_role: "PATIENT"
 * └── etc.
 *
 * NOTES:
 * - L'intercepteur s'exécute pour TOUTES les requêtes
 * - Même /api/auth/login (mais pas de token encore)
 * - Si pas de token → Header non ajouté (normal pour login/register)
 */
public class JwtInterceptor implements Interceptor {

    private final Context context;

    /**
     * Constructeur
     *
     * @param context Context Android (pour accès SharedPreferences)
     */
    public JwtInterceptor(Context context) {
        this.context = context;
    }

    /**
     * Intercepter la requête et ajouter le token JWT
     *
     * PROCESSUS:
     * 1. Récupérer token depuis SharedPreferences
     * 2. Si token existe:
     *    └── Ajouter header "Authorization: Bearer {token}"
     * 3. Envoyer la requête
     *
     * @param chain Chaîne d'intercepteurs OkHttp
     * @return Response HTTP
     * @throws IOException Exception réseau
     */
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // 1. Récupérer la requête originale
        Request originalRequest = chain.request();

        // 2. Récupérer le token JWT depuis SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        // 3. Si pas de token, envoyer requête telle quelle
        // (Normal pour /login et /register)
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // 4. Créer nouvelle requête avec header Authorization
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        // 5. Envoyer la requête modifiée
        return chain.proceed(newRequest);
    }
}