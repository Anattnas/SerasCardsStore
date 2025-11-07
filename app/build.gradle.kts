//------------------------------------------
// plugins: define los plugins que usará este módulo del proyecto
// Cada plugin habilita funcionalidades específicas para compilar y ejecutar la app
//------------------------------------------
plugins {
    alias(libs.plugins.android.application) // Plugin principal de Android para compilar apps
    alias(libs.plugins.kotlin.android)      // Plugin para usar Kotlin en Android
// Add the Google services Gradle plugin
    //id("com.google.gms.google-services")
// Se modifico la linea anterior por:
    alias(libs.plugins.googleService)      // Plugin de Google Services para integrar Firebase
}

/*
id("com.google.gms.google-services") Aplica el plugin de Google Services directamente al módulo app.
Ese plugin es necesario para que Android Studio pueda leer el archivo google-services.json de Firebase y configurar automáticamente los servicios (Analytics, Authentication, Firestore, etc.).
id(...) es la forma manual/directa de aplicar un plugin usando su identificador.
___________________________
alias(libs.plugins.googleService) Aplica el mismo plugin pero usando el alias definido en libs.versions.toml:
[plugins]
googleService = { id = "com.google.gms.google-services", version.ref = "googleServicesversion" }

No necesitas escribir el id y la versión directamente aquí.
Si en el futuro se quiere actualizar la versión de google-services, solo cambias en libs.versions.toml y todos los módulos que usan el alias se actualizan automáticamente.
Funcionalmente, hace lo mismo que la línea original, pero de forma más limpia y centralizada.
*/

//------------------------------------------
// android: configuración principal del módulo Android
//------------------------------------------
android {
    namespace = "com.example.serascardsstore" // Define el namespace del proyecto, usado para R y otros recursos
    compileSdk = 36                           // Define la versión del SDK con la que se compila la app

    //------------------------------------------
    // defaultConfig: configuración básica del app
    //------------------------------------------
    defaultConfig {
        applicationId = "com.example.serascardsstore" // ID único de la app
        minSdk = 24                                   // Versión mínima de Android que soporta la app
        targetSdk = 36                                // Versión de Android para la cual se optimiza la app
        versionCode = 1                               // Número interno de versión de la app
        versionName = "1.0"                           // Nombre de la versión visible para el usuario

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Define el runner de pruebas para Android Instrumented Tests
    }

    //------------------------------------------
    // buildTypes: define los distintos tipos de compilación
    //------------------------------------------
    buildTypes {
        release {
            isMinifyEnabled = false // Indica si se debe ofuscar y reducir el código en release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Configuración por defecto de Proguard
                "proguard-rules.pro"                                                    // Reglas personalizadas de Proguard
            )
        }
    }
    //------------------------------------------
    // compileOptions: define la compatibilidad con Java
    //------------------------------------------
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Código fuente compatible con Java 11
        targetCompatibility = JavaVersion.VERSION_11 // Bytecode compatible con Java 11
    }
    //------------------------------------------
    // kotlinOptions: configuración de Kotlin
    //------------------------------------------
    kotlinOptions {
        jvmTarget = "11" // Compila Kotlin para generar bytecode compatible con JVM 11
    }
    //------------------------------------------
    // buildFeatures: características adicionales de compilación
    //------------------------------------------
    buildFeatures{
        viewBinding = true // Habilita ViewBinding para enlazar vistas de manera segura
    }
}
//------------------------------------------
// dependencies: define todas las dependencias de librerías que usará el módulo
//------------------------------------------
dependencies {
    // Librerías de AndroidX y UI
    implementation(libs.androidx.core.ktx)         // Extensiones Kotlin para Android core
    implementation(libs.androidx.appcompat)        // Compatibilidad de UI entre versiones de Android
    implementation(libs.material)                  // Material Design Components
    implementation(libs.androidx.activity)         // Manejo de Activities con compatibilidad
    implementation(libs.androidx.constraintlayout) // Layouts flexibles con ConstraintLayout

    // Librerías de Firebase agregadas para registro de usuarios
    implementation(libs.firebaseAuth)              // Firebase Authentication
    implementation(libs.firebaseDatabase)          // Firebase Realtime Database
    implementation(libs.loginGoogle)               // dependencia para login con Google
    implementation(libs.glide)                     // Agrega la librería Glide al proyecto para poder usarla en el código de la app.
    implementation(libs.firebaseStorage)           // Agrega la dependencia Firebase Storage al proyecto
    implementation(libs.ccp)                       // Agrega la librería Country Code Picker (CCP) al proyecto para poder usarla en el módulo actual
    testImplementation(libs.junit)                 // JUnit para pruebas unitarias
    // Se borro/comento la siguiente linea:
    // implementation("com.google.firebase:firebase-analytics") // Cuando se usa BoM, no es necesario especificar versiones en dependencias de Firebase

    // Librerías para pruebas instrumentadas (UI)
    androidTestImplementation(libs.androidx.junit)         // Extensión de JUnit para pruebas instrumentadas
    androidTestImplementation(libs.androidx.espresso.core) // Espresso para pruebas de UI
}