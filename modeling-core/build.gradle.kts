import org.jetbrains.dokka.gradle.DokkaTask

val javaVersion: String by project

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `java-test-fixtures`
    `maven-publish`
    signing
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

val javadocJar by tasks.registering(Jar::class) {
    val dokkaHtml by tasks.getting(DokkaTask::class)
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "modeling-core"

            from(components["kotlin"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())

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

signing {
    sign(publishing.publications["mavenJava"])
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
