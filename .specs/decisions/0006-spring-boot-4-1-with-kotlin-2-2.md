# 0006: Spring Boot 4.1 with Kotlin held at 2.2

- **Status:** accepted, 2026-10-08. Supersedes [0001](0001-spring-boot-4-0-and-kotlin-2-2.md).

## Context

- Shadleaf ([0007](0007-ui-follows-wim-deblauwe-with-shadleaf.md)) supports only Spring Boot 4.1.x. Every row of
  the compatibility table in its README says so for releases 0.1.0 through 0.7.0. Taming Thymeleaf 4.0.0 also
  targets Spring Boot 4.1.1.
- Spring Boot 4.1.1 manages Kotlin 2.3.21. Viaduct 2.x's module plugin fails the build for Kotlin above 2.2
  ([architecture](../architecture.md#constraints)).
- Spring Boot 4.0.8 and 4.1.1 manage the same Spring Framework (7.0.9) and Thymeleaf (3.1.5). They differ in Spring
  Security (7.0.7 vs 7.1.1). Checked on Maven Central, 2026-10-08.

## Decision

- Use Spring Boot 4.1.1.
- Override its managed Kotlin version to 2.2.21, so the compiler, the standard library and the Viaduct check agree.

## Consequences

- Shadleaf runs on a Spring Boot version it supports, which matches Wim Deblauwe's own setup.
- Kotlin runs one minor version behind what Spring Boot manages. **Unverified:** that Spring Boot 4.1.1 works with
  Kotlin 2.2.21. S00 proves it before anything else is built.
- **Fallback if S00 disproves it:** Spring Boot 4.0.8, where Shadleaf is untested. Testing Shadleaf on 4.0.x then
  becomes an upstream contribution.
- The override is removed in the slice that adopts a Viaduct release supporting Kotlin 2.3.
