// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.1"

    // Pin versions for subprojects
    kotlin("jvm") version "1.7.10" apply false
    id("org.jetbrains.dokka") version "1.7.10" apply false
}

repositories {
    mavenCentral()
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
}

// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "com.larsreimann"
    version = "3.1.1"

    repositories {
        mavenCentral()
    }
}
