import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.koin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.koin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room schema

    }

    // Room schema location for source sets
    sourceSets {
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation ("androidx.datastore:datastore-preferences:1.1.7")
    // Remove duplicate paging dependencies at the top
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // WorkManager with Coroutines
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler) // Use hilt-compiler, not androidx.hilt.compiler
    implementation(libs.androidx.work.runtime.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Retrofit & Moshi
    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // OkHttp & Logging
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Gson Converter
    implementation(libs.converter.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Room testing
    testImplementation(libs.androidx.room.testing)

    // Kotlin Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // MockK for testing
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.11")

    // Turbine for Flow testing
    testImplementation(libs.turbine)

    // Coil
    implementation(libs.coil.compose)

    // Charts - Use Vico instead of MPAndroidChart for Compose
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    implementation(libs.vico.views)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Gson
    implementation(libs.gson)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Adaptive Navigation
    implementation(libs.androidx.adaptive.navigation)
}

//dependencies {
//
//    implementation ("androidx.paging:paging-runtime-ktx:3.3.6")
//    implementation ("androidx.paging:paging-compose:3.3.6")
//    // DataStore
//    implementation("androidx.datastore:datastore-preferences:1.1.7")
//    // Accompanist Pager for tab & horizontal pager
//    // Core
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.kotlinx.coroutines.android)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//
//    // WorkManager with Coroutines
//    implementation("androidx.hilt:hilt-work:1.1.0")
//    ksp("androidx.hilt:hilt-compiler:1.1.0")
//    implementation("androidx.work:work-runtime-ktx:2.10.2")
//
//
//    // Compose UI
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.ui.tooling.preview)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//    // Lifecycle (optional if you're using LiveData)
//    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    implementation(libs.androidx.compose.runtime.livedata)
//
//    // Navigation
//    implementation(libs.androidx.navigation.compose)
//
//    // Paging
//    implementation(libs.androidx.paging.runtime.ktx)
//    implementation(libs.androidx.paging.compose)
//
//    // Hilt
//    implementation(libs.hilt.android)
//    implementation(libs.androidx.hilt.navigation.compose)
//    ksp(libs.hilt.compiler)
//
//    // Retrofit & Moshi
//    implementation(libs.retrofit)
//    implementation(libs.moshi)
//    implementation(libs.moshi.kotlin)
//    implementation(libs.retrofit.converter.moshi)
//    ksp(libs.moshi.kotlin.codegen)
//    // OkHttp & Logging - Add these missing dependencies
//    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
//
//    // Gson Converter - Add this missing dependency
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//
//
//    // Room
//    implementation(libs.androidx.room.runtime)
//    implementation(libs.androidx.room.ktx)
//    ksp(libs.androidx.room.compiler)
//
//    // Room testing
//    testImplementation("androidx.room:room-testing:2.6.1")
//
//    // Kotlin Coroutines Test
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
//
//    // MockK for testing
//    testImplementation("io.mockk:mockk:1.13.8")
//    testImplementation("io.mockk:mockk-agent-jvm:1.13.8")
//
//    // Turbine for Flow testing
//    testImplementation("app.cash.turbine:turbine:1.0.0")
//
//    // Coil (Optional for image loading)
//    implementation(libs.coil.compose)
//
//    // Charts
//    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
//    implementation("com.patrykandpatrick.vico:compose:1.13.1")
//    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
//    implementation("com.patrykandpatrick.vico:core:1.13.1")
//    implementation("com.patrykandpatrick.vico:views:1.13.1")
//
//    // Testing
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//
//    // Gson
//    implementation("com.google.code.gson:gson:2.13.1")
////    implementation("com.squareup.retrofit2:retrofit:2.9.0")
////    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
////    implementation("com.squareup.okhttp3:okhttp:4.9.3")
////    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
////    implementation("androidx.room:room-runtime:2.5.2")
////    kapt("androidx.room:room-compiler:2.5.2")
////    implementation("androidx.room:room-ktx:2.5.2")
////    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
////    implementation("com.google.dagger:hilt-android:2.48")
//}
