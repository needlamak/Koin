plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")

}

android {
    namespace = "com.pricealert"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51") // Use ksp for Hilt compiler

    // For Hilt with ViewModel (if you're injecting into ViewModels)
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0") // For Fragments
    implementation("androidx.hilt:hilt-work:1.2.0") // For WorkManager
    ksp("androidx.hilt:hilt-compiler:1.2.0") // KSP for androidx.hilt compiler

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // AndroidX DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // AndroidX WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Kotlin Coroutines (if not implicitly pulled by WorkManager/DataStore)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}