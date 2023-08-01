// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    // Pin versions for subprojects
    kotlin("jvm") version "1.9.0" apply false
    id("org.jetbrains.dokka") version "1.8.20" apply false
    id("org.jetbrains.kotlinx.kover") version "0.7.3" apply false
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
