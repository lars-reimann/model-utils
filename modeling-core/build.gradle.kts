val javaVersion: String by project

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    kotlin("jvm")
    `maven-publish`
    `java-test-fixtures`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "modeling-core"
            from(components["kotlin"])
            pom {
                url.set("https://github.com/lars-reimann/modeling")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/lars-reimann/modeling/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("Lars Reimann")
                        email.set("mail@larsreimann.com")
                    }
                }
                scm {
                    url.set("https://github.com/lars-reimann/modeling.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.3")

    testFixturesImplementation(kotlin("test"))
    testFixturesImplementation("io.kotest:kotest-assertions-core-jvm:5.0.3")
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks {
    test {
        useJUnitPlatform()
    }
}
