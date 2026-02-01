plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.cabinet.cabinetmedical"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cabinet.cabinetmedical"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true  // Active la génération de BuildConfig
    }

    buildTypes {
        debug {
            // Environnement de DÉVELOPPEMENT (local)
            // URL WSL2: Obtenir avec "ip addr show eth0 | grep inet"
            buildConfigField("String", "BASE_URL", "\"http://172.25.135.62:8080/\"")
            buildConfigField("String", "ENVIRONMENT", "\"DEV\"")
        }

        release {
            // Environnement de PRODUCTION (Railway)
            buildConfigField("String", "BASE_URL", "\"https://android-mobile-java-springboot-app-backend-production.up.railway.app/\"")
            buildConfigField("String", "ENVIRONMENT", "\"PROD\"")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════
    // DÉPENDANCES DE BASE ANDROID
    // ═══════════════════════════════════════════════════════
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ═══════════════════════════════════════════════════════
    // RETROFIT - Client REST API
    // ═══════════════════════════════════════════════════════
    // Retrofit : Communication HTTP avec backend
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson Converter : Parse JSON automatiquement
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp : Client HTTP (utilisé par Retrofit)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Logging Interceptor : Voir les requêtes HTTP dans logcat (debug)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ═══════════════════════════════════════════════════════
    // GSON - JSON Serialization/Deserialization
    // ═══════════════════════════════════════════════════════
    implementation("com.google.code.gson:gson:2.10.1")

    // ═══════════════════════════════════════════════════════
    // RECYCLERVIEW - Listes (médecins, RDV, etc.)
    // ═══════════════════════════════════════════════════════
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView : Cards Material Design
    implementation("androidx.cardview:cardview:1.0.0")

    // ═══════════════════════════════════════════════════════
    // MATERIAL COMPONENTS - UI moderne
    // ═══════════════════════════════════════════════════════
    // Extended Material (floating buttons, etc.)
    implementation("com.google.android.material:material:1.11.0")

    // ═══════════════════════════════════════════════════════
    // TESTS
    // ═══════════════════════════════════════════════════════
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

