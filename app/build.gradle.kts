plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.kotlinKapt)
}

android {
    namespace = "com.app.routineturboa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.routineturboa"
        minSdk = 30
        targetSdk = 34
        versionCode = 4
        versionName = "4.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: project.properties["RELEASE_KEY_ALIAS"] as String
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: project.properties["RELEASE_KEY_PASSWORD"] as String
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: project.properties["RELEASE_STORE_FILE"] as String)
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: project.properties["RELEASE_STORE_PASSWORD"] as String
        }
        create("dev") {
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: project.properties["RELEASE_KEY_ALIAS"] as String
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: project.properties["RELEASE_KEY_PASSWORD"] as String
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: project.properties["RELEASE_STORE_FILE"] as String)
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: project.properties["RELEASE_STORE_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            applicationIdSuffix = ".release"
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }

        create("dev") {
            signingConfig = signingConfigs.getByName("dev")
            applicationIdSuffix = ".dev"
            isDebuggable = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // Hilt dependencies using KAPT
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    implementation(libs.material) // Add the Material Components library here

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.msal)
    implementation(libs.volley)
    implementation(libs.graph)
    implementation(libs.sqlite)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3.window.size)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
