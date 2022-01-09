// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0-RC"

    // Pin versions for subprojects
    kotlin("jvm") version "1.6.10" apply false
    id("org.jetbrains.dokka") version "1.6.10" apply false
}

repositories {
    mavenCentral()
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
    generateReportOnCheck.set(true)
}

// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "com.larsreimann"
    version = "2.0.0"

    repositories {
        mavenCentral()
    }
}
