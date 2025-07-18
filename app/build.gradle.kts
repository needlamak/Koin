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
        minSdk = 25
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
        debug {
            buildConfigField("boolean", "DEBUG", "true")
        }

        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "DEBUG", "true")
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

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    // App Startup
    implementation("androidx.startup:startup-runtime:1.2.0")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
    // Remove duplicate paging dependencies at the top
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // WorkManager with Coroutines
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.compose.material)
    ksp(libs.hilt.compiler) // Use hilt-compiler, not androidx.hilt.compiler
    implementation(libs.androidx.work.runtime.ktx)
    androidTestImplementation ("androidx.work:work-testing:2.10.2")

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
