plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("app.cash.sqldelight")
    id("org.sirekanyan.version-checker")
}

android {
    namespace = "org.sirekanyan.outline"
    compileSdk = 34
    defaultConfig {
        applicationId = "org.sirekanyan.outline"
        minSdk = 21
        targetSdk = 34
        versionCode = (property("appVersionCode") as String).toInt()
        versionName = property("appVersionName") as String
        setProperty("archivesBaseName", "$applicationId-$versionName-$versionCode")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = true
    }
    lint {
        warningsAsErrors = true
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
        }
    }
}

dependencies {

    // compose
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3:1.1.1")

    // ktor
    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // sqldelight
    implementation("app.cash.sqldelight:android-driver:2.0.0")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")

}

sqldelight {
    databases {
        create("OutlineDatabase") {
            packageName.set("org.sirekanyan.outline.db")
        }
    }
}
