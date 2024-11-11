plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.pms.rcuapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pms.rcuapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.gson)
    implementation (libs.androidx.room.runtime)
    implementation (libs.retrofit)
    implementation (libs.rxjava.core)
    implementation (libs.rxjava.android)
    implementation (libs.rxandroid)

    implementation (libs.okhttp)
     implementation (libs.logging.interceptor)

    implementation ("com.google.firebase:firebase-messaging-ktx:23.0.3")
    implementation ("com.google.firebase:firebase-bom:29.3.1")
    implementation ("com.google.firebase:firebase-messaging:23.0.3")
}