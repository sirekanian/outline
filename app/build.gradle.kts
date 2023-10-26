import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

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
        archivesName.set("$applicationId-$versionName-$versionCode")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard.pro")
            signingConfig = signingConfigs.create("release") {
                storeFile = System.getenv("SIGNING_KEYSTORE_FILE")?.let(::file)
                storePassword = System.getenv("SIGNING_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }
    flavorDimensions += "store"
    productFlavors {
        create("fdroid") {
            dimension = "store"
        }
        create("play") {
            dimension = "store"
            listOf("ACRA_URI", "ACRA_LOGIN", "ACRA_PASSWORD").forEach { key ->
                buildConfigField("String", key, System.getenv(key)?.let { "\"$it\"" } ?: "null")
            }
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
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // compose
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.material3:material3:1.1.2")

    // ktor
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // sqldelight
    implementation("app.cash.sqldelight:android-driver:2.0.0")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")

    // crash reporting
    add("playImplementation", "ch.acra:acra-http:5.11.3")

}

sqldelight {
    databases {
        create("OutlineDatabase") {
            packageName.set("org.sirekanyan.outline.db")
        }
    }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name.replaceFirstChar(Char::titlecase)
        val verifyTask = task("verify${variantName}Environment") {
            doLast {
                buildSet {
                    if (variant.buildType == "release") {
                        add("SIGNING_KEYSTORE_FILE")
                        add("SIGNING_KEYSTORE_PASSWORD")
                        add("SIGNING_KEY_ALIAS")
                        add("SIGNING_KEY_PASSWORD")
                    }
                    if (variant.flavorName == "play") {
                        add("ACRA_URI")
                        add("ACRA_LOGIN")
                        add("ACRA_PASSWORD")
                    }
                }.forEach { key ->
                    if (System.getenv(key).isNullOrEmpty()) {
                        error("Please specify $key environment variable")
                    }
                }
            }
        }
        afterEvaluate {
            tasks.getByName("assemble$variantName").dependsOn(verifyTask)
        }
    }
}
