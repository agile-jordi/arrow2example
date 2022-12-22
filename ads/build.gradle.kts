import Dependencies.arrowCore
import Dependencies.kotestRunnerJunit
import Dependencies.kotlinXCoroutinesCore
import Dependencies.kotlinXSerializationJson
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.6/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    kotlin("jvm") version "1.8.0-RC2"
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
//    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}


dependencies {

    implementation(project(":codecs"))
    implementation(arrowCore)
    implementation(kotlinXSerializationJson)
    implementation(kotlinXCoroutinesCore)
    testImplementation(kotestRunnerJunit)
}

testing {


    suites {
        // Configure the built-in test suite
        @Suppress("UnstableApiUsage")
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            // Use Kotlin Test test framework
//            useKotlinTest("1.7.10")
//            dependencies {
//                // Use newer version of JUnit Engine for Kotlin Test
//                implementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
//            }
        }
    }
}
