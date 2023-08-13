plugins {
    val kotlinVersion = "1.9.0"
    id("com.android.application") version "8.1.0" apply false
    kotlin("android") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("com.squareup.sqldelight") version "1.5.5" apply false
    id("org.sirekanyan.version-checker") version "1.0.7" apply false
}
