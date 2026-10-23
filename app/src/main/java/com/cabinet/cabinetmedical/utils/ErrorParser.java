package com.cabinet.cabinetmedical.utils;

import com.cabinet.cabinetmedical.model.response.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;

import retrofit2.Response;

/**
 * Transforme les erreurs de l'API et du réseau en messages lisibles.
 * Le message rédigé par l'API (format ErrorResponse) est prioritaire ; à défaut, un
 * message selon le code HTTP.
 */
public final class ErrorParser {

    private static final Gson GSON = new Gson();

    private ErrorParser() {
    }

    /** Message d'erreur d'une réponse HTTP non réussie. */
    public static String parse(Response<?> response) {
        String fromBody = parseErrorMessage(response.errorBody());
        return fromBody != null ? fromBody : messageForStatus(response.code());
    }

    /**
     * Message de l'API contenu dans le corps d'erreur : détail des erreurs de validation
     * (une par ligne) ou message général. Null si le corps est absent ou illisible.
     */
    public static String parseErrorMessage(ResponseBody errorBody) {
        if (errorBody == null) {
            return null;
        }
        try {
            ErrorResponse error = GSON.fromJson(errorBody.string(), ErrorResponse.class);
            if (error == null) {
                return null;
            }
            if (error.getErrors() != null && !error.getErrors().isEmpty()) {
                return String.join("\n", error.getErrors());
            }
            return error.getMessage();
        } catch (IOException | JsonParseException | IllegalStateException e) {
            return null;
        }
    }

    static String messageForStatus(int code) {
        switch (code) {
            case 400:
                return "Requête invalide.";
            case 401:
                return "Session expirée, reconnectez-vous.";
            case 403:
                return "Accès refusé.";
            case 404:
                return "Élément introuvable.";
            case 409:
                return "Action impossible dans l'état actuel (déjà fait ou déjà réservé).";
            case 429:
                return "Trop de tentatives. Réessayez dans quelques minutes.";
            default:
                return code >= 500 ? "Erreur du serveur, réessayez plus tard." : "Erreur inattendue (" + code + ").";
        }
    }

    public static String getNetworkErrorMessage(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            return "Impossible de joindre le serveur. Vérifiez qu'il est démarré.";
        }
        if (throwable instanceof SocketTimeoutException) {
            return "La connexion a expiré. Réessayez.";
        }
        if (throwable instanceof UnknownHostException) {
            return "Vérifiez votre connexion Internet.";
        }
        if (throwable instanceof IOException) {
            return "Erreur de connexion. Vérifiez votre réseau.";
        }
        return "Une erreur inattendue s'est produite. Réessayez.";
    }
}
