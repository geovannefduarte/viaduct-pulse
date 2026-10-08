# S00: Skeleton

- **Status:** ready
- **Chapter:** none
- **Links:**
  - [architecture](../architecture.md)
  - [UI guidelines](../ui-guidelines.md)
  - [ADR 0002](../decisions/0002-embedded-postgres.md), [ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md), [ADR 0007](../decisions/0007-ui-follows-wim-deblauwe-with-shadleaf.md), [ADR 0008](../decisions/0008-java-25-via-asdf.md)

## Goal

`./gradlew bootRun` on a fresh clone starts a Spring Boot app backed by a real embedded Postgres, with no Docker
and no Node. It serves a Shadleaf-styled home page that loads a database status panel through htmx.

## Viaduct concepts

None. This slice proves the stack before Viaduct is added.

## Scope

**In:**
- **Toolchain check first.** Before anything else, a Kotlin app must compile and start with:
  - Spring Boot 4.1.1, with its managed Kotlin overridden to 2.2.21
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

**Out:** Viaduct, sync logic, CI, GraphiQL.

## Design

- **Starting Postgres.** A Spring `@Configuration` starts `EmbeddedPostgres` and exposes its `DataSource`. Spring
  Boot's `DataSource` auto-configuration steps aside because a `DataSource` bean exists. **Unverified:** the zonky
  builder options for a fixed data directory that isn't wiped on start. Check them first.
- **Status card.** `/status` serves both callers by checking what the request targets
  ([UI guidelines](../ui-guidelines.md#htmx)).
- **Tests** start embedded Postgres with a temporary data directory.

## Acceptance criteria

- [ ] `./gradlew :app:dependencyInsight --dependency kotlin-stdlib` resolves 2.2.21, and the app compiles and starts
  on Spring Boot 4.1.1.
- [ ] Gradle and the app run on JDK 25 (`./gradlew -version` and the startup log), and the class files have major
  version 68, which is Java 24 (`javap -v`).
- [ ] `./gradlew bootRun` from a clean clone, with Docker not running and no Node installed, serves the home page.
- [ ] The home page renders Shadleaf components, and the theme toggle switches between light and dark.
- [ ] The status card shows the Postgres version. It is filled by an htmx request to `/status`.
- [ ] `GET /status` without htmx headers returns the full page.
- [ ] A row written to `sync_state` survives an app restart.
- [ ] `./gradlew build` passes, including tests and `spotlessCheck`.
- [ ] The resolved classpath has no `graphql-java` artifact (`./gradlew :app:dependencies`).
- [ ] No template uses the Thymeleaf Layout Dialect, and no page loads a script from a CDN.

## Tests

- **`@WebMvcTest` with HtmlUnit:** `/` renders the status placeholder, found by `id`. `/status` returns the fragment
  with htmx headers and the full page without them.
- **`@SpringBootTest`:** the context starts with embedded Postgres, and the Flyway migration has applied.

## Open questions

None. The port is 8080; the license is Apache-2.0.

## Verification

—
