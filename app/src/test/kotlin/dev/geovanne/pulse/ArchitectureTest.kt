package dev.geovanne.pulse

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ArchitectureTest {
    private val classes =
        ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("dev.geovanne.pulse")

    @Test
    fun `modules only use each other's public API and have no cycles`() {
        ApplicationModules.of(PulseApplication::class.java).verify()
    }

    @Test
    fun `domain depends on no framework and on no other layer`() {
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "org.flywaydb..",
                "java.sql..",
                "javax.sql..",
                "dev.geovanne.pulse..application..",
                "dev.geovanne.pulse..web..",
                "dev.geovanne.pulse..persistence..",
                "dev.geovanne.pulse.platform..",
            ).check(classes)
    }

    @Test
    fun `application depends on no adapter`() {
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("dev.geovanne.pulse..web..", "dev.geovanne.pulse..persistence..")
            .check(classes)
    }

    @Test
    fun `web and persistence adapters don't depend on each other`() {
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..web..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("dev.geovanne.pulse..persistence..")
            .check(classes)
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..persistence..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("dev.geovanne.pulse..web..")
            .check(classes)
    }
}
