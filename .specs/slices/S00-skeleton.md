# S00: Skeleton

- **Status:** done
- **Chapter:** none
- **Links:**
  - [architecture](../architecture.md)
  - [UI guidelines](../ui-guidelines.md)
  - [ADR 0002](../decisions/0002-embedded-postgres.md), [ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md), [ADR 0007](../decisions/0007-ui-follows-wim-deblauwe-with-shadleaf.md), [ADR 0008](../decisions/0008-java-25-via-asdf.md), [ADR 0009](../decisions/0009-domain-packages-with-ports-and-adapters.md)

## Goal

`./gradlew bootRun` on a fresh clone starts a Spring Boot app backed by a real embedded Postgres, with no Docker
and no Node. It serves a Shadleaf-styled home page that loads a database status panel through htmx.

## Viaduct concepts

None. This slice proves the stack before Viaduct is added.

## Scope

**In:**
- **Toolchain check first.** Before anything else, a Kotlin app must compile and start with:
  - Spring Boot 4.1.1, with Kotlin held at 2.2.21
    ([ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md));
  - JDK 25 from `.tool-versions`, with bytecode target 24 ([ADR 0008](../decisions/0008-java-25-via-asdf.md)).

  If either fails, stop and decide on the fallback.
- **Build:**
  - Gradle 9.1.0 wrapper, Kotlin DSL.
  - `gradle/libs.versions.toml` with the versions in [architecture](../architecture.md#stack).
  - `.tool-versions` pinning a Java 25 JDK, and a Gradle toolchain of 25.
  - Kotlin `jvmTarget = 24` and Java `release = 24`.
  - Spotless with ktlint, applied to Kotlin sources and the Gradle Kotlin scripts. `./gradlew check` runs
    `spotlessCheck`.
- **Projects:** `:app` and `:sync`. `:sync` is empty apart from its build file, so the layout is in place.
- **Code structure:** domain packages with ports and adapters
  ([ADR 0009](../decisions/0009-domain-packages-with-ports-and-adapters.md)). The status card belongs to a `system`
  domain, embedded Postgres to `platform`, and the home page to `home`. An architecture test enforces the rules.
- **Makefile:** `make` lists the targets. `make run`, `make test`, `make build`, `make check`, `make format` and
  `make clean` wrap Gradle; `make setup` installs the pinned JDK with asdf; `make stop` stops every app and embedded
  Postgres started from this checkout; `make db-reset` stops them, then deletes the local database. Make sets `JAVA_HOME` from asdf when asdf has a Java version for this folder.
- **Web stack:**
  - Spring MVC and Thymeleaf.
  - Shadleaf 0.7.0 and `htmx-spring-boot-thymeleaf` 5.2.0.
  - The htmx 2.0.11 webjar with `webjars-locator-lite`.
- **Layout:**
  - A parameterized layout fragment (`templates/layout/main.html`) with Shadleaf's `theme-script` and `assets`
    fragments, a site header and a theme toggle.
  - `static/css/app.css` for page layout, built on Shadleaf's tokens.
- **Text:** in `i18n/messages.properties` from the start.
- **Database:**
  - Embedded Postgres starts before the `DataSource` is created.
  - Its data directory is `.pulse/pgdata`, gitignored and kept across restarts.
  - Flyway migration `V1` creates `sync_state` ([data sync](../data-sync.md#rules)).
- **Home page:** `GET /` is the full page. `GET /status` returns a status card fragment (Postgres version, applied
  migrations) for htmx and the full page otherwise.
- **Server port:** 8080.
- **Profiles:** a `local` profile with template and resource caching off.
- **Error pages:** `templates/error/404.html` and `5xx.html`.
- **Repository files:**
  - `.gitignore`, which keeps the existing `CLAUDE.local.md` entry.
  - `LICENSE` with the Apache License 2.0 text.
  - `README.md` covering: what Pulse is, how to run it, the license, and where the data comes from (public metadata
    of the `airbnb/viaduct` GitHub repository, no emails).
  - The Commands section of `CLAUDE.md`.

- **CI and pull requests:**
  - `.github/workflows/ci.yml` runs `make build` on Ubuntu and macOS for every pull request and every push to `main`.
    Actions are pinned to commit SHAs. Gradle caching uses the open-source `basic` provider.
  - `.github/pull_request_template.md`, with a Screenshots section for UI changes.

**Out:** Viaduct, sync logic, the Viaduct version matrix (S03), GraphiQL.

## Design

- **Starting Postgres.** A Spring `@Configuration` starts `EmbeddedPostgres` and exposes its `DataSource`. Spring
  Boot's `DataSource` auto-configuration steps aside because a `DataSource` bean exists. zonky's builder keeps a fixed
  data directory with `setDataDirectory` and `setCleanDataDirectory(false)`; it runs `initdb` only when
  `postgresql.conf` is missing. `setRegisterShutdownHook(false)` leaves shutdown to Spring, which closes the pool
  before Postgres. Verified in the 2.2.2 sources and by the restart and shutdown checks below.
- **Status card.** `/status` serves both callers by checking what the request targets
  ([UI guidelines](../ui-guidelines.md#htmx)).
- **Tests** start embedded Postgres with a temporary data directory.

## Acceptance criteria

- [x] `./gradlew :app:dependencyInsight --dependency kotlin-stdlib` resolves 2.2.21, and the app compiles and starts
  on Spring Boot 4.1.1.
- [x] Gradle and the app run on JDK 25 (`./gradlew -version` and the startup log), and the class files have major
  version 68, which is Java 24 (`javap -v`).
- [x] `./gradlew bootRun` from a clean clone, with Docker not running and no Node installed, serves the home page.
- [x] The home page renders Shadleaf components, and the theme toggle switches between light and dark.
- [x] The status card shows the Postgres version. It is filled by an htmx request to `/status`.
- [x] `GET /status` without htmx headers returns the full page.
- [x] A row written to `sync_state` survives an app restart.
- [x] `./gradlew build` passes, including tests and `spotlessCheck`.
- [x] The resolved classpath has no `graphql-java` artifact (`./gradlew :app:dependencies`).
- [x] No template uses the Thymeleaf Layout Dialect, and no page loads a script from a CDN.
- [x] `make` lists the targets, and `make test` and `make run` work from a fresh shell with no `JAVA_HOME` set.
- [x] CI passes on this slice's pull request, on Ubuntu and macOS.
- [x] The architecture test fails when a `domain` package depends on Spring or on an adapter, and when one domain
  reaches into another domain's internals.

## Tests

- **`@WebMvcTest` with HtmlUnit:** `/` renders the status placeholder, found by `id`. `/status` returns the fragment
  with htmx headers and the full page without them.
- **`@SpringBootTest`:** the context starts with embedded Postgres, and the Flyway migration has applied.
- **Architecture:** Spring Modulith verifies the modules; ArchUnit checks the layer directions.

## Open questions

None. The port is 8080; the license is Apache-2.0.

## Verification

Checked on 2026-10-08 on macOS arm64.

- **Kotlin 2.2.21 on Boot 4.1.1.** `dependencyInsight` resolves `kotlin-stdlib:2.2.21` on `runtimeClasspath`. No
  explicit `kotlin.version` is needed: Spring Boot's Gradle plugin (`KotlinPluginAction`) sets it from the applied
  Kotlin plugin, and removing the explicit property left the result at 2.2.21. The startup log shows Spring Boot
  4.1.1.
- **JDK 25, bytecode 24.** `./gradlew -version` reports launcher and daemon JVM 25.0.4.1 (Corretto). The startup log
  says "using Java 25.0.4.1". `javap -v` on `PulseApplication.class` shows major version 68. The build prints no
  JVM-target warnings, `build-logic` included.
- **Clean clone, no Docker, no Node.** A copy of the tracked files ran `./gradlew :app:bootRun` with only `/usr/bin`
  and `/bin` on `PATH`, so neither `node` nor `docker` was reachable, and Docker was not running. `GET /` returned
  200; Flyway logged PostgreSQL 18.6 and applied V1.
- **Status card.** `GET /status` with `HX-Request` and `HX-Target: status-card` returns only `#status-card` with the
  version 18.6 and the V1 migration. Without htmx headers, with another target, or as a history-restore request it
  returns the full page. Covered by `StatusControllerTest` and `PulseApplicationTest`.
- **Restart.** `EmbeddedPostgresPersistenceTest` writes a `sync_state` row, stops Postgres, starts it on the same
  directory and reads the row back. A second `bootRun` on the same `.pulse/pgdata` logged "Schema "public" is up to
  date".
- **Shutdown order.** With zonky's default shutdown hook, Postgres stopped about 0.9 s before the connection pool
  closed. With `setRegisterShutdownHook(false)`, the log shows graceful shutdown, then the pool, then Postgres, and no
  `postgres` process remains.
- **Build.** `./gradlew build` runs 23 tests (0 failures) and `spotlessCheck` for the root, `app` and `sync`.
  Positive controls: a misformatted Kotlin file and a misformatted `build-logic` script each failed `spotlessCheck`.
  A template using the Layout Dialect and a CDN script failed both `StackRulesTest` template rules.
- **No graphql-java.** `./gradlew :app:dependencies` lists no `graphql` artifact. `StackRulesTest` fails if
  `graphql.GraphQL` loads.
- **Theme toggle, and htmx filling the card in a browser:** checked by the owner in a browser: the card loaded on its
  own, and the dark theme applied. The HtmlUnit tests cover the rendered markup with JavaScript off.
- **Corrections to earlier claims,** now fixed in the ADRs:
  - [ADR 0002](../decisions/0002-embedded-postgres.md) said `embedded-postgres` 2.2.2 ships Postgres 18.6. It pulls
    14.22.0 binaries for x86 platforms only; the build imports `embedded-postgres-binaries-bom` 18.6.0 and adds the
    arm64 artifacts.
  - [ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md) called for overriding the managed Kotlin version.
    Applying the 2.2.21 Kotlin plugin is enough; see the first item.
- **Makefile.** `make` lists nine targets. With `JAVA_HOME` unset, `make build` passed and Gradle reported launcher
  and daemon JVM 25 under `make`, both from the tool shell and from a fresh `zsh -i`. Control: with asdf off `PATH`,
  the same command fell back to the system JDK 21, so the asdf lookup is what selects 25. `make run` served `/` and
  the htmx status card.
- **Architecture.** `ArchitectureTest` passes on the domain layout. Positive controls, each removed afterwards: a
  Spring annotation in `system.domain` failed the domain rule; `application` using a `persistence` class failed the
  application rule; a class in `home` using `system.persistence` failed Spring Modulith's `verify()` alone.
- **Review fixes:**
  - **Durability.** zonky starts Postgres with `fsync` and `synchronous_commit` off.
    `EmbeddedPostgresDurabilityTest` failed with `fsync` = `off`, then passed once the launcher set both to `on`.
  - **Request errors.** Checked in headless Chrome against the running app, with `/status` made to fail through
    request interception. A 500 response, or no response within the 10 s htmx timeout, shows the error message and a
    retry button and sets `aria-busy` to `false`; "Try again" then loads the card. With `request-errors.js` blocked, the
    same 500 left the card on "Loading…", as reported in review. The page-level fallback for regions without an error
    slot has no caller yet and is unverified.
  - **Apostrophes.** `ErrorPagesTest` hit the real 404 page and saw `doesn''t`; it passes with single apostrophes.
    `StackRulesTest` now checks the bundle, and failed when a doubled apostrophe was added to a message without
    arguments.
  - **Temporary data directories.** Tests use `pulse.postgres.ephemeral=true`, where zonky deletes its directory on
    close. A full test run left no new directories (11 left by earlier runs were removed).
  - **Guards.** The template rules check sample strings, including `th:src="@{https://…}"` and
    `~{sl/layout::assets}`. The domain rule is an allowlist and failed on a `jakarta` import in `system.domain`.
- **`make stop`.** In a scratch copy, it stopped a running app with SIGTERM (graceful shutdown, port freed) and left no
  Postgres behind. After `kill -9` on the app, the orphaned Postgres made the next `make run` fail ("Is another
  postmaster … running"); `make stop` shut it down and the next run reused the data. It finds Postgres by the working
  directory, which is its data directory, so another checkout's Postgres was left running. `shellcheck` passes, and it
  ran under macOS's bash 3.2.
- **CI.** Green on Ubuntu and macOS for the merged head of #2
  ([run 37850579643](https://github.com/geovannefduarte/viaduct-pulse/actions/runs/37850579643)) and for the push to
  `main` ([run 37851219715](https://github.com/geovannefduarte/viaduct-pulse/actions/runs/37851219715)).
- **Tag:** S00 has no chapter, so no `chNN` tag.
