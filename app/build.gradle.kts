plugins {
    id("pulse.spring-boot-app")
}

dependencyManagement {
    imports {
        mavenBom(
            libs.embedded.postgres.binaries.bom
                .get()
                .toString(),
        )
        mavenBom(
            libs.spring.modulith.bom
                .get()
                .toString(),
        )
    }
}

dependencies {
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.kotlin.reflect)
    implementation(libs.shadleaf)
    implementation(libs.htmx.spring.boot.thymeleaf)
    implementation(libs.embedded.postgres)
    runtimeOnly(libs.embedded.postgres.binaries.darwin.arm64v8)
    runtimeOnly(libs.embedded.postgres.binaries.linux.arm64v8)
    runtimeOnly(libs.htmx.webjar)
    runtimeOnly(libs.webjars.locator.lite)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.htmlunit)
    testImplementation(libs.spring.modulith.starter.test)
}
