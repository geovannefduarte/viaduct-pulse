# 0001: Spring Boot 4.0 with Kotlin 2.2

- **Status:** accepted, 2026-10-07

## Context

- The app is built with Kotlin. Ktor, Micronaut and Spring Boot were all candidates.
- Viaduct's own demo apps already cover Ktor (`demoapps/ktor-starter`) and Micronaut (`demoapps/starwars`).
- No Viaduct demo uses Spring: `demoapps/spring-starter/README.md` calls itself a placeholder that does not yet use
  the Spring Framework.
- The latest Spring Boot, 4.1.1, manages Kotlin 2.3.21. Viaduct 2.x's module plugin fails the build for Kotlin above
  2.2 ([architecture](../architecture.md#constraints)).

## Decision

Use Spring Boot 4.0.8, which manages Kotlin 2.2.21, with Spring MVC.

## Consequences

- Pulse covers the framework Viaduct's demos don't, which raises its value as a compatibility canary.
- The app is one minor version behind Spring Boot's latest. It moves forward in the slice that adopts a Viaduct
  release supporting Kotlin 2.3.
- Spring's dependency management can change versions of libraries Viaduct expects the app to provide. The canary CI
  catches that.
