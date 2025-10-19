// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

// Add the dependency for the Google services Gradle plugin
    // id("com.google.gms.google-services") version "4.4.3" apply false
// Modificamos la linea anterior linea 7 por la siguiente:
    alias(libs.plugins.googleService) apply false
}

/*
La linea 7 declara el plugin de Firebase Google Services directamente en el build.gradle.kts de nivel superior.
Usa el id del plugin (com.google.gms.google-services) y la versión (4.4.3) escrita ahí mismo.
apply false significa: “declaro este plugin aquí, pero no lo apliques todavía; lo aplicaré en el módulo app”.

En resumen: era una forma directa de registrar el plugin con su versión.
_____________________________________________________________________________
La nueva linea, linea 9 en lugar de escribir el id y la versión directo aquí, estás usando el Version Catalog (libs.versions.toml).
libs.plugins.googleService es un alias que definiste en libs.versions.toml así:
[plugins]
googleService = { id = "com.google.gms.google-services", version.ref = "googleServicesversion" }
Ese alias ya sabe qué id tiene el plugin y qué versión usar (4.4.3 en este caso).

Igual que antes, apply false significa que solo se declara aquí y se aplicará luego en el build.gradle.kts del módulo app.

En resumen: la línea modificada hace exactamente lo mismo, pero de una manera más limpia y centralizada,
porque las versiones ya no están regadas por todos los Gradle, sino organizadas en libs.versions.toml.
*/