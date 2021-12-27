val javaVersion: String by project

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.2")
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks {
    test {
        useJUnitPlatform()
    }
}
