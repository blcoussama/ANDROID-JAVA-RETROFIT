plugins {
    alias(libs.plugins.android.application)
}

// URL de l'API injectée dans BuildConfig.BASE_URL, par type de build :
// - debug : http://10.0.2.2:8080/ (machine hôte vue depuis l'émulateur),
//   modifiable avec -PapiBaseUrl=http://192.168.1.20:8080/ pour un téléphone sur le réseau local ;
// - release : HTTPS obligatoire, à fournir avec -PreleaseApiBaseUrl=https://...
val debugApiBaseUrl: String = providers.gradleProperty("apiBaseUrl").getOrElse("http://10.0.2.2:8080/")
val releaseApiBaseUrl: String =
    providers.gradleProperty("releaseApiBaseUrl").getOrElse("https://api.cabinet-medical.invalid/")

require(releaseApiBaseUrl.startsWith("https://")) { "releaseApiBaseUrl doit commencer par https://" }
require(debugApiBaseUrl.endsWith("/") && releaseApiBaseUrl.endsWith("/")) { "L'URL de l'API doit finir par /" }

android {
    namespace = "com.cabinet.cabinetmedical"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cabinet.cabinetmedical"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"$debugApiBaseUrl\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"$releaseApiBaseUrl\"")
            isMinifyEnabled = true
            isShrinkResources = true
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

    testOptions {
        // android.util.Log & co. renvoient des valeurs par défaut dans les tests JVM
        unitTests.isReturnDefaultValues = true
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Client REST : Retrofit + Gson sur OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
