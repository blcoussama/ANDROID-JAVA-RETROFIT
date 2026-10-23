# Règles R8 pour la build release (minification activée).

# Modèles sérialisés par Gson par réflexion : noms de champs à conserver (JSON de l'API).
-keep class com.cabinet.cabinetmedical.model.** { *; }

# Retrofit lit les types génériques et les annotations de l'interface ApiService.
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keep,allowobfuscation interface com.cabinet.cabinetmedical.api.ApiService

# Garder les numéros de ligne dans les traces, sans exposer les noms de fichiers source.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
