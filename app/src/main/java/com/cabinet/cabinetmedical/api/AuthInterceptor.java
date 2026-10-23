package com.cabinet.cabinetmedical.api;

import androidx.annotation.NonNull;

import com.cabinet.cabinetmedical.session.TokenStore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Ajoute {@code Authorization: Bearer <jeton>} aux requêtes et gère l'expiration de session.
 *
 * <p>Si l'API répond 401 à une requête authentifiée par ce jeton (jeton expiré ou révoqué),
 * la session est supprimée et {@link SessionExpiredListener} est prévenu (retour à l'écran de
 * connexion). Un 403 n'est pas une expiration : l'action est refusée, l'utilisateur reste
 * connecté et l'écran affiche « Accès refusé ». Les routes de connexion et d'inscription ne
 * reçoivent jamais de jeton ; une requête qui porte déjà son propre en-tête (déconnexion)
 * n'est pas modifiée.</p>
 */
public class AuthInterceptor implements Interceptor {

    public interface SessionExpiredListener {
        void onSessionExpired();
    }

    private final TokenStore tokenStore;
    private final SessionExpiredListener listener;

    public AuthInterceptor(TokenStore tokenStore, SessionExpiredListener listener) {
        this.tokenStore = tokenStore;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.header("Authorization") != null || isPublicAuthRoute(request)) {
            return chain.proceed(request);
        }
        String token = tokenStore.getToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(request);
        }
        Response response = chain.proceed(request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build());
        if (response.code() == 401 && token.equals(tokenStore.getToken())) {
            tokenStore.clear();
            if (listener != null) {
                listener.onSessionExpired();
            }
        }
        return response;
    }

    private static boolean isPublicAuthRoute(Request request) {
        String path = request.url().encodedPath();
        return path.endsWith("/api/auth/login") || path.endsWith("/api/auth/register");
    }
}
