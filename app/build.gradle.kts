plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.suyogbauskar.calmora"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.suyogbauskar.calmora"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("com.airbnb.android:lottie:6.6.2")
    implementation("com.github.f0ris.sweetalert:library:1.5.6")
}