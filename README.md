# Cabinet médical : application Android de prise de rendez-vous

Application Android (Java) pour les patients, les médecins et l'administrateur d'un cabinet médical : réservation, confirmation et annulation de rendez-vous, gestion des créneaux et des comptes.

[![CI](https://github.com/blcoussama/ANDROID-JAVA-RETROFIT/actions/workflows/ci.yml/badge.svg)](https://github.com/blcoussama/ANDROID-JAVA-RETROFIT/actions/workflows/ci.yml)

L'application consomme l'API REST [SPRING-BOOT-JWT-REST-API](https://github.com/blcoussama/SPRING-BOOT-JWT-REST-API) (Spring Boot, PostgreSQL, JWT), qui applique toutes les règles d'accès et de gestion.

## Fonctionnalités

| Rôle | Écrans |
|---|---|
| **Patient** | Inscription et connexion ; liste des médecins ; heures libres d'un médecin à une date ; réservation avec motif ; mes rendez-vous avec statut, et annulation d'un rendez-vous à venir (raison facultative) |
| **Médecin** | Mes rendez-vous : confirmer, annuler avec raison ; mes créneaux hebdomadaires : ajouter, modifier, supprimer |
| **Administrateur** | Tableau de bord (rendez-vous du jour et de la semaine, répartition par statut, nombre de comptes) ; patients et médecins : liste, fiche, création, modification, suppression ; tous les rendez-vous : confirmer, annuler avec raison |

Le compte administrateur est créé par l'API au démarrage (variables `ADMIN_EMAIL` / `ADMIN_PASSWORD`, voir le README de l'API) ; l'inscription publique crée toujours un compte patient.

## Stack

Java 17, Android SDK (minSdk 24, compileSdk 36, targetSdk 35), AppCompat et Material Components, RecyclerView, Retrofit 2.12, OkHttp 4.12, Gson 2.13, Android Keystore. Build Gradle 8.13 (AGP 8.13). Tests : JUnit 4, MockWebServer.

## Architecture

```
app/src/main/java/com/cabinet/cabinetmedical/
├── api/        ApiClient (Retrofit unique, contexte applicatif), ApiService (routes), AuthInterceptor (jeton, 401)
├── session/    SessionManager (session chiffrée), KeystoreSessionCipher (AES-GCM, clé Keystore), SessionNavigator
├── model/      requêtes et réponses JSON de l'API
├── ui/auth     Splash (vérifie le jeton), Login, Register
├── ui/patient  médecins, heures libres, réservation, mes rendez-vous
├── ui/doctor   rendez-vous du médecin, créneaux
├── ui/admin    tableau de bord, patients, médecins, rendez-vous
├── ui/common   boîte de dialogue d'annulation, couleurs de statut
└── utils/      ErrorParser (messages d'erreur), Validators, DateTimes
```

Chaque écran appelle l'API avec Retrofit (`enqueue`) ; l'identité de l'utilisateur est portée par le jeton, l'application n'envoie jamais l'identifiant du patient ou du médecin connecté pour décider d'un accès (`/api/appointments/me`, réservation sans `patientId`, annulation où l'API déduit qui annule).

## Lancement local

1. Démarrer l'API (voir [SPRING-BOOT-JWT-REST-API](https://github.com/blcoussama/SPRING-BOOT-JWT-REST-API#lancement-local)) : `docker compose up --build` dans le dépôt de l'API, qui écoute sur le port 8080.
2. Ouvrir ce projet dans Android Studio, ou en ligne de commande avec un SDK Android installé :

   ```bash
   ./gradlew installDebug        # émulateur ou appareil connecté
   ```

3. Se connecter avec le compte administrateur du seed, créer un médecin et ses créneaux, puis s'inscrire comme patient depuis l'écran de connexion.

URL de l'API (`BuildConfig.BASE_URL`, définie dans `app/build.gradle.kts`) :

| Build | URL par défaut | Changer |
|---|---|---|
| debug | `http://10.0.2.2:8080/` (la machine hôte vue depuis l'émulateur) | `./gradlew installDebug -PapiBaseUrl=http://192.168.1.20:8080/` pour un téléphone sur le même réseau |
| release | `https://api.cabinet-medical.invalid/` (volontairement injoignable) | `./gradlew assembleRelease -PreleaseApiBaseUrl=https://votre-api/` ; HTTPS obligatoire, le build échoue sinon |

La build release est minifiée (R8) et n'est pas signée : la signature se configure à part.

## Sécurité

- **Stockage** : le jeton et le profil sont sérialisés puis chiffrés en AES-256-GCM avec une clé non exportable de l'Android Keystore avant d'être écrits dans les SharedPreferences ; rien n'est stocké en clair (l'ancien fichier en clair des versions précédentes est supprimé).
- **Sauvegardes** : `allowBackup="false"` et règles d'extraction qui excluent toutes les données (sauvegarde cloud et transfert d'appareil).
- **Réseau** : `network_security_config` qui refuse HTTP en clair en release ; le clair n'est autorisé que dans la variante debug (`src/debug`), pour l'API locale.
- **Journaux** : journal HTTP en debug uniquement, limité aux en-têtes, avec `Authorization` et les cookies masqués (ni mot de passe, ni jeton, ni motif de consultation dans logcat).
- **Session** : un 401 de l'API sur une requête authentifiée efface la session et ramène à la connexion (« session expirée ») ; un 403 affiche « Accès refusé » sans déconnecter. Au démarrage, le jeton est vérifié (`/api/auth/validate`). La déconnexion révoque les jetons côté API (`POST /api/auth/logout`).
- **Mémoire** : le client Retrofit ne garde que le contexte applicatif (plus de référence à une Activity).
- **Saisie** : règles de mot de passe identiques à celles de l'API (8 caractères, une lettre et un chiffre), l'API restant la référence.

## Tests et qualité

```bash
./gradlew assembleDebug lint testDebugUnitTest
```

21 tests unitaires JVM : `SessionManagerTest` (rien en clair, session illisible supprimée, rien d'écrit si le chiffrement échoue), `AuthInterceptorTest` avec MockWebServer (jeton ajouté, jamais envoyé à la connexion, 401 => déconnexion, 403 => session conservée), `ErrorParserTest`, `ValidatorsTest`, `AppointmentResponseTest`. Lint : aucune erreur.

La CI GitHub Actions lance la même commande, un audit `osv-scanner` des dépendances embarquées dans l'APK (`app/gradle.lockfile`) et `gitleaks` sur tout l'historique.

## Captures

Captures à venir.

## Limites connues

- Pas de tests instrumentés ni de tests d'interface (Espresso) : seule la logique hors écran est testée.
- Les textes sont écrits en dur dans les layouts et le code (avertissements lint `HardcodedText`) : l'application n'est pas traduisible en l'état.
- Pas de jeton de rafraîchissement : après une heure, l'utilisateur doit se reconnecter.
- Les listes ne sont pas paginées et ne se rafraîchissent pas automatiquement ; pas de notifications.
- `targetSdk` 35 : passage à 36 non testé sur appareil.
- Les dates sont interprétées dans le fuseau du téléphone (l'API applique le fuseau du cabinet).

## Origine du projet

Projet personnel : application mobile de prise de rendez-vous pour un cabinet médical, développée de janvier à début février 2026 avec son API.

### Ce que j'ai ajouté / corrigé (octobre 2026)

- Session chiffrée avec l'Android Keystore, exclusion des sauvegardes, HTTPS imposé en release, journaux HTTP réservés au debug et masqués.
- URL de l'API par type de build (l'ancienne URL d'hébergement, supprimée, était codée en dur), client Retrofit sans fuite d'Activity, minification R8.
- Gestion de l'expiration de session (401) et de l'accès refusé (403), déconnexion avec révocation côté API.
- Alignement sur l'API sécurisée (routes `/me`, réservation sans `patientId`, annulation par `POST`), annulation par le patient, raison d'annulation pour le médecin et l'administrateur.
- Dépendances nettoyées (Material en double) et mises à jour, verrouillage des dépendances, tests unitaires, CI, README.
