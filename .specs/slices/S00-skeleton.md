# S00: Skeleton

- **Status:** draft
- **Chapter:** none
- **Links:** [architecture](../architecture.md), [ADR 0001](../decisions/0001-spring-boot-4-0-and-kotlin-2-2.md),
  [ADR 0002](../decisions/0002-embedded-postgres.md), [ADR 0003](../decisions/0003-thymeleaf-and-htmx.md)

## Goal

`./gradlew bootRun` on a fresh clone starts a Spring Boot app backed by a real embedded Postgres, with no Docker. It
serves a home page that loads a database status panel through htmx.

## Viaduct concepts

None. This slice proves the stack before Viaduct is added.

## Scope

**In:**
- Gradle 9.1.0 wrapper, Kotlin DSL, `gradle/libs.versions.toml` with the versions in
  [architecture](../architecture.md#stack), JDK 21 toolchain.
- Projects `:app` and `:sync`. `:sync` is empty apart from its build file, so the layout is in place.
- Spring Boot 4.0.8 with Spring MVC, Thymeleaf and `htmx-spring-boot-thymeleaf`.
- Embedded Postgres started before the `DataSource` is created. The data directory is `.pulse/pgdata`, gitignored,
  and kept across restarts.
- Flyway migration `V1` creating `sync_state` ([data sync](../data-sync.md#rules)).
- A home page with a panel htmx loads from `/status`: the Postgres version and the number of applied migrations.
- `.gitignore` and a `README.md` with how to run.
- The Commands section of `CLAUDE.md`.

**Out:** Viaduct, sync logic, CI, styling beyond a classless CSS file.

## Design

- **Starting Postgres.** A Spring `@Configuration` starts `EmbeddedPostgres` and exposes its `DataSource`.
  Spring's `DataSourceAutoConfiguration` steps aside because a `DataSource` bean exists. **Unverified:** the zonky
  builder options for a fixed data directory that isn't wiped on start. Check them first.
- **Serving htmx.** htmx 2.0.11 is a single static file, served from the app or a CDN. That choice is an open
  question below.
- **Tests** start the same embedded Postgres with a temporary data directory.

## Acceptance criteria

- [ ] `./gradlew bootRun` from a clean clone, with Docker not running, serves `http://localhost:8080`.
- [ ] The home page shows the Postgres version, which comes from the htmx request to `/status`.
- [ ] A row written to `sync_state` survives an app restart.
- [ ] `./gradlew build` passes, including tests.
- [ ] The resolved classpath has no `graphql-java` artifact (`./gradlew :app:dependencies`).

## Tests

- A Spring Boot test that starts the context with embedded Postgres and checks that `/` renders.
- A test that `/status` returns the fragment with a Postgres version, both with and without the `HX-Request`
  header.
- A test that the Flyway migration applied.

## Open questions

- htmx from a CDN, or vendored into `static/`? Vendoring keeps the app working offline, which fits the
  one-command principle.
- The port: 8080?
- A CSS choice for the guided pages, for example Pico CSS (classless). It can wait until S02.

## Verification

—
