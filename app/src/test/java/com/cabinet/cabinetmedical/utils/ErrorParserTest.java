package com.cabinet.cabinetmedical.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ErrorParserTest {

    private static Response<Object> error(int code, String json) {
        return Response.error(code, ResponseBody.create(json, MediaType.get("application/json")));
    }

    @Test
    public void usesApiMessageWhenPresent() {
        assertEquals("Cette heure est déjà réservée",
                ErrorParser.parse(error(409, "{\"status\":409,\"message\":\"Cette heure est déjà réservée\"}")));
    }

    @Test
    public void joinsValidationErrors() {
        String json = "{\"status\":400,\"message\":\"Erreur de validation\","
                + "\"errors\":[\"password: trop court\",\"email: invalide\"]}";
        assertEquals("password: trop court\nemail: invalide", ErrorParser.parse(error(400, json)));
    }

    @Test
    public void fallsBackOnStatusCode() {
        assertEquals("Accès refusé.", ErrorParser.parse(error(403, "")));
        assertEquals("Session expirée, reconnectez-vous.", ErrorParser.parse(error(401, "pas du json")));
        assertEquals("Erreur du serveur, réessayez plus tard.", ErrorParser.parse(error(503, "")));
        assertEquals("Trop de tentatives. Réessayez dans quelques minutes.", ErrorParser.parse(error(429, "")));
    }

    @Test
    public void networkErrors() {
        assertEquals("Impossible de joindre le serveur. Vérifiez qu'il est démarré.",
                ErrorParser.getNetworkErrorMessage(new ConnectException()));
        assertEquals("La connexion a expiré. Réessayez.",
                ErrorParser.getNetworkErrorMessage(new SocketTimeoutException()));
    }
}
