package com.cabinet.cabinetmedical.session;

/** Accès minimal au jeton, utilisé par l'intercepteur HTTP (et remplaçable dans les tests). */
public interface TokenStore {

    /** Jeton courant, ou null si personne n'est connecté. */
    String getToken();

    /** Supprime la session (déconnexion ou jeton refusé par l'API). */
    void clear();
}
