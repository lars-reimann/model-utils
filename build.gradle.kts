// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0-RC"

    // Pin versions for subprojects
    kotlin("jvm") version "1.6.10" apply false
}

repositories {
    mavenCentral()
}


// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "com.larsreimann"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}
