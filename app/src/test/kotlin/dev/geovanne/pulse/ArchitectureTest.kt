package dev.geovanne.pulse

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ArchitectureTest {
    private val pulseClasses =
        ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("dev.geovanne.pulse")

    @Test
    fun `modules only use each other's public API and have no cycles`() {
        ApplicationModules.of(PulseApplication::class.java).verify()
    }

    @Test
    fun `domain depends only on the JDK, Kotlin and other domain types`() {
        classes()
            .that()
            .resideInAPackage("dev.geovanne.pulse..domain..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage("java..", "kotlin..", "org.jetbrains.annotations..", "dev.geovanne.pulse..domain..")
            .check(pulseClasses)
    }

    @Test
    fun `application depends on no adapter`() {
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("dev.geovanne.pulse..web..", "dev.geovanne.pulse..persistence..")
            .check(pulseClasses)
    }

    @Test
    fun `web and persistence adapters don't depend on each other`() {
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..web..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("dev.geovanne.pulse..persistence..")
            .check(pulseClasses)
        noClasses()
            .that()
            .resideInAPackage("dev.geovanne.pulse..persistence..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("dev.geovanne.pulse..web..")
            .check(pulseClasses)
    }
}
