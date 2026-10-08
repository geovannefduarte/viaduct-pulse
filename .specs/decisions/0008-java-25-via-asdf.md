# 0008: Java 25 through asdf

- **Status:** accepted, 2026-10-08

## Context

- Java 25 is the latest LTS release.
- The repository should pin its own JDK so the machine-wide default doesn't matter.
- Gradle 9.1.0 supports Java 25 both for running Gradle and for toolchains, per Gradle's compatibility table.
- Kotlin 2.2.21 can emit bytecode up to Java 24 only: its Gradle `JvmTarget` enum ends at `JVM_24`. Kotlin 2.3.21
  adds `JVM_25`. Both enums were checked in the published `kotlin-gradle-plugin-api` jars.
- Viaduct's CI tests Java 17 and 21. Viaduct on Java 25 is untested there.

## Decision

- Pin a Java 25 JDK in `.tool-versions` for asdf.
- Use a Gradle toolchain of 25.
- Compile Kotlin with `jvmTarget = 24`, and compile Java with `release = 24`, until a Viaduct release allows Kotlin
  2.3. Then move both to 25.

## Consequences

- The app runs on Java 25. Its bytecode targets 24, which Java 25 runs.
- **Unverified:** that the Kotlin 2.2.21 compiler and KSP run on JDK 25. S00 checks the compiler and S02 checks KSP.
- Pulse reports Viaduct-on-Java-25 problems upstream, as compatibility data.
- **Fallback:** if a library blocks Java 25, switch `.tool-versions` and the toolchain to 21 and record what blocked it
  in [upstream findings](../upstream-findings.md).
