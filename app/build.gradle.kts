import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.suyogbauskar.calmora"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.suyogbauskar.calmora"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath =
                localProperties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
            if (!keystorePath.isNullOrEmpty()) {
                storeFile = file(keystorePath)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
                keyPassword =
                    localProperties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
            } else {
                println("⚠️ Keystore is missing, falling back to debug signing.")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig =
                signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Auth
    implementation(libs.firebase.auth)
    //Firestore
    implementation(libs.firebase.firestore)

    implementation(libs.play.services.auth)
    implementation(libs.lottie)
    implementation(libs.library)
    implementation(libs.viewpager2)
    implementation(libs.material.v161)
    implementation(libs.dotsindicator)


    implementation(libs.mpandroidchart)


}
