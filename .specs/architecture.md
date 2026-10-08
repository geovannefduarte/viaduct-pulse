# Architecture

## Stack

Versions were checked on Maven Central, the npm registry and services.gradle.org on 2026-10-07.

| Layer | Choice | Version | Decision |
|---|---|---|---|
| JDK | toolchain | 21 | Viaduct supports 17+ |
| Language | Kotlin | 2.2.21 | [ADR 0001](decisions/0001-spring-boot-4-0-and-kotlin-2-2.md) |
| Framework | Spring Boot, Spring MVC | 4.0.8 | [ADR 0001](decisions/0001-spring-boot-4-0-and-kotlin-2-2.md) |
| GraphQL server | Viaduct | 2.0.0, the only 2.x release | — |
| Annotation processing | KSP | 2.2.21-2.0.5 | required by Viaduct's module plugin |
| Database | Postgres via `io.zonky.test:embedded-postgres` | 2.2.2, Postgres 18.6 binaries | [ADR 0002](decisions/0002-embedded-postgres.md) |
| Migrations | Flyway | managed by Spring Boot | — |
| Data access | Spring `JdbcClient`, hand-written SQL | managed by Spring Boot | [ADR 0002](decisions/0002-embedded-postgres.md) |
| Templates | Thymeleaf with `htmx-spring-boot-thymeleaf` | 5.2.0 | [ADR 0003](decisions/0003-thymeleaf-and-htmx.md) |
| Browser interactivity | htmx | 2.0.11, the npm `latest` tag | [ADR 0003](decisions/0003-thymeleaf-and-htmx.md) |
| Build | Gradle, Kotlin DSL, version catalog | 9.1.0 | — |

Gradle 9.1.0 is what Viaduct's own demo apps used when 2.0.0 was released. Gradle 9.8.1 is current.

## Project layout (planned)

```
viaduct-pulse/
├── settings.gradle.kts        # applies Viaduct's settings plugin: includeViaductApplication { … }
├── gradle/libs.versions.toml
├── app/                       # the Spring Boot app; becomes the Viaduct application project in S02
├── sync/                      # pulse-sync: git and GitHub API → Postgres (S01)
├── modules/                   # Viaduct tenant modules; S02 adds the first one, S08 splits it
└── seed/                      # the committed seed snapshot (S01)
```

**Unverified:** that Viaduct's settings plugin accepts a Spring Boot application project next to a non-Viaduct
project such as `:sync`. S02 confirms it.

## Request path

1. The browser sends a request to a Spring MVC controller.
2. The controller calls `Viaduct.execute(ExecutionInput, schemaId)`, a suspend function. **Unverified:** that a
   Spring MVC 4.0.8 suspend handler calls it cleanly. `executeAsync` returning a `CompletableFuture` is the fallback.
3. Viaduct runs the resolvers in `modules/*`, which read Postgres through `JdbcClient`.
4. Guided pages post queries with htmx and get back a server-rendered fragment
   ([learning path](learning-path.md#query-card)).

## Constraints

Each constraint was verified on 2026-10-07 unless marked otherwise.

- **Kotlin 2.2 at most, while on Viaduct 2.x.** Viaduct's module plugin fails the build for Kotlin outside
  [1.9, 2.2]. See `validateKotlinVersion` in
  `gradle-plugins/module/src/main/kotlin/viaduct/gradle/ViaductModulePlugin.kt` in airbnb/viaduct.
  - Spring Boot 4.1.1 manages Kotlin 2.3.21; Spring Boot 4.0.8 manages 2.2.21.
  - The check predates the 2.0.0 release and is still on main. It has not been tested against the published 2.0.0
    plugin.
- **Never add a second graphql-java.**
  - `com.airbnb.viaduct:runtime:2.0.0` bundles graphql-java (`graphql.*`) and Guice (`com.google.inject.*`)
    without relocating them.
  - The POMs of `api:2.0.0` and `runtime:2.0.0` declare no dependencies.
  - Spring Boot 4.0.8 manages graphql-java 25.0 for spring-graphql. Adding spring-graphql would put two copies on the
    classpath.
- **The app declares some runtime dependencies itself.** The Viaduct jars exclude Kotlin and kotlinx. Viaduct's
  demo apps add kotlinx-coroutines-core, kotlinx-coroutines-jdk8, reactive-streams, kotlin-reflect and
  jackson-databind themselves.
  - Under Spring Boot 4.0.8 these resolve to Boot-managed versions: coroutines 1.10.2 and reactive-streams 1.0.4.
  - **Unverified:** that Viaduct 2.0.0 works with coroutines 1.10.2.
- **Jackson 2 and Jackson 3 side by side.** Spring Boot 4.0.8 defaults to Jackson 3 (`tools.jackson`, 3.1.5) and
  also manages Jackson 2 (2.21.5). Viaduct's demo apps use jackson-databind 2. **Unverified:** that they coexist
  cleanly.
- **Viaduct's Gradle plugins come from the Gradle Plugin Portal:**
  - `com.airbnb.viaduct.settings-gradle-plugin`
  - `com.airbnb.viaduct.application-gradle-plugin`
  - `com.airbnb.viaduct.module-gradle-plugin`

  Each module keeps its schema in `src/main/viaduct/schema/*.graphqls`.

## Observability

- **Per-field timings:** Viaduct emits Micrometer timers (`viaduct.execution`, `viaduct.operation`,
  `viaduct.field`) when its builder is given a `MeterRegistry`. These names come from Viaduct's main branch; S12
  confirms them against the pinned release.
- **SQL per request:** a datasource proxy captures each statement and tags it with the request that ran it.
- **No public hook in 2.x:** query plans, DataLoader batch sizes and per-resolver instrumentation
  ([ADR 0005](decisions/0005-public-viaduct-api-only.md)).

## Upgrades

- Each Viaduct upgrade gets its own slice.
- The canary CI (S03) runs the tests against the pinned version, the latest release and snapshots.
- When a Viaduct release supports Kotlin 2.3, its upgrade slice also moves to the latest Spring Boot.
