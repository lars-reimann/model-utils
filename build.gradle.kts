// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    // Pin versions for subprojects
    kotlin("jvm") version "1.8.22" apply false
    id("org.jetbrains.dokka") version "1.8.10" apply false
    id("org.jetbrains.kotlinx.kover") version "0.7.0" apply false
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
