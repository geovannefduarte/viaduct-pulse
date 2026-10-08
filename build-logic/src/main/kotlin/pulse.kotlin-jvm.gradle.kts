import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("pulse.formatting")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

// Kotlin 2.2 can't emit Java 25 bytecode; move both targets to 25 with Kotlin 2.3 (ADR 0008).
kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_24
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 24
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
