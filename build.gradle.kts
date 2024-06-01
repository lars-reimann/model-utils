// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    // Pin versions for subprojects
    kotlin("jvm") version "2.0.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("org.jetbrains.kotlinx.kover") version "0.8.0" apply false
}

repositories {
    mavenCentral()
}

// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "com.larsreimann"
    version = "3.1.1"

    repositories {
        mavenCentral()
    }
}
