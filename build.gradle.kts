plugins {
    val kotlinVersion = "1.9.22"
    id("com.android.application") version "8.2.2" apply false
    kotlin("android") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("app.cash.sqldelight") version "2.0.1" apply false
    id("org.sirekanyan.version-checker") version "1.0.10" apply false
}
