import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
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
        debug {
            val props = Properties().also {
                it.load(File("debug.properties").also(File::createNewFile).inputStream())
            }
            isDebuggable = props.getProperty("DEBUGGABLE").toBoolean()
            applicationIdSuffix = ".debug"
            buildConfigField("boolean", "DEBUG", "true")
            listOf("DEBUG_SERVER1", "DEBUG_SERVER2").forEach { key ->
                buildConfigField("String", key, props.getProperty(key)?.let { "\"$it\"" } ?: "null")
            }
        }
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
            resValue("string", "project_id", System.getenv("GOOGLE_PROJECT_ID") ?: "")
            resValue("string", "google_app_id", System.getenv("GOOGLE_APP_ID") ?: "")
            resValue("string", "google_api_key", System.getenv("GOOGLE_API_KEY") ?: "")
            resValue("string", "com.crashlytics.android.build_id", "1")
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
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.1.2")

    // ktor
    implementation("io.ktor:ktor-client-okhttp:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
    implementation("org.slf4j:slf4j-simple:2.0.11")

    // sqldelight
    implementation("app.cash.sqldelight:android-driver:2.0.1")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")

    // crash reporting
    add("playImplementation", "com.google.firebase:firebase-crashlytics:18.6.1")

}

sqldelight {
    databases {
        create("OutlineDatabase") {
            packageName.set("org.sirekanyan.outline.db")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
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
                        add("GOOGLE_PROJECT_ID")
                        add("GOOGLE_APP_ID")
                        add("GOOGLE_API_KEY")
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
