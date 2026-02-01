package com.cabinet.cabinetmedical.utils;

import com.cabinet.cabinetmedical.model.response.ErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;

/**
 * ErrorParser - Utilitaire pour parser et formater les messages d'erreur
 *
 * RESPONSABILITÉS:
 * - Parser ErrorResponse du backend
 * - Convertir exceptions techniques en messages user-friendly
 * - Éviter d'afficher des détails techniques (IP, ports, etc.)
 *
 * UTILISATION:
 * // Erreur HTTP (response.errorBody())
 * String message = ErrorParser.parseErrorMessage(response.errorBody());
 *
 * // Erreur réseau (onFailure Throwable)
 * String message = ErrorParser.getNetworkErrorMessage(throwable);
 */
public class ErrorParser {

    /**
     * Parse le message d'erreur depuis la réponse backend
     *
     * UTILISÉ POUR:
     * - Erreurs HTTP (400, 401, 409, 500, etc.)
     * - Quand response.errorBody() est disponible
     *
     * @param errorBody ResponseBody de Retrofit
     * @return Message d'erreur du backend (ou message par défaut)
     */
    public static String parseErrorMessage(ResponseBody errorBody) {
        try {
            if (errorBody != null) {
                String errorJson = errorBody.string();
                Gson gson = new Gson();
                ErrorResponse error = gson.fromJson(errorJson, ErrorResponse.class);

                if (error != null) {
                    // Si erreurs de validation existent, les afficher
                    if (error.getErrors() != null && !error.getErrors().isEmpty()) {
                        // Joindre toutes les erreurs avec des sauts de ligne
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.getErrors().size(); i++) {
                            sb.append(error.getErrors().get(i));
                            if (i < error.getErrors().size() - 1) {
                                sb.append("\n");
                            }
                        }
                        return sb.toString();
                    }

                    // Sinon, retourner le message général
                    if (error.getMessage() != null) {
                        return error.getMessage();
                    }
                }
            }
        } catch (Exception e) {
            // Si parsing échoue, retourner message générique
        }

        return "Erreur serveur";
    }

    /**
     * Convertir une exception réseau en message user-friendly
     *
     * UTILISÉ POUR:
     * - onFailure(Call, Throwable) de Retrofit
     * - Erreurs réseau (backend offline, timeout, pas d'internet)
     *
     * EXEMPLES:
     * ConnectException → "Impossible de se connecter au serveur"
     * SocketTimeoutException → "La connexion a expiré"
     * UnknownHostException → "Vérifiez votre connexion Internet"
     *
     * ÉVITE D'AFFICHER:
     * ❌ "Failed to connect to /172.25.135.62:8080"
     * ❌ "Unable to resolve host"
     * ✅ Messages simples et compréhensibles
     *
     * @param throwable Exception capturée par onFailure
     * @return Message d'erreur user-friendly
     */
    public static String getNetworkErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Erreur inconnue";
        }

        // Erreur de connexion (serveur offline, IP inaccessible)
        if (throwable instanceof ConnectException) {
            return "Impossible de se connecter au serveur. Vérifiez que le serveur est démarré.";
        }

        // Timeout (serveur trop lent ou ne répond pas)
        if (throwable instanceof SocketTimeoutException) {
            return "La connexion a expiré. Réessayez.";
        }

        // Pas d'accès réseau ou DNS ne résout pas
        if (throwable instanceof UnknownHostException) {
            return "Vérifiez votre connexion Internet.";
        }

        // IOException générique (problème réseau)
        if (throwable instanceof IOException) {
            return "Erreur de connexion. Vérifiez votre réseau.";
        }

        // Autres erreurs (parsing JSON, etc.)
        return "Une erreur inattendue s'est produite. Réessayez.";
    }
}
