import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_24
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 24
}

dependencies {
    implementation(libs.kotlin.allopen.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.spotless.plugin)
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management.plugin)
}
