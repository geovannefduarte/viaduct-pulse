# S00: Skeleton

- **Status:** draft
- **Chapter:** none
- **Links:**
  - [architecture](../architecture.md)
  - [UI guidelines](../ui-guidelines.md)
  - [ADR 0002](../decisions/0002-embedded-postgres.md), [ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md), [ADR 0007](../decisions/0007-ui-follows-wim-deblauwe-with-shadleaf.md)

## Goal

`./gradlew bootRun` on a fresh clone starts a Spring Boot app backed by a real embedded Postgres, with no Docker
and no Node. It serves a Shadleaf-styled home page that loads a database status panel through htmx.

## Viaduct concepts

None. This slice proves the stack before Viaduct is added.

## Scope

**In:**
- **Kotlin check first.** Spring Boot 4.1.1 with its managed Kotlin overridden to 2.2.21, compiling and starting a
  Kotlin app ([ADR 0006](../decisions/0006-spring-boot-4-1-with-kotlin-2-2.md)). If this fails, stop and decide on
  the fallback before doing anything else.
- **Build:**
  - Gradle 9.1.0 wrapper, Kotlin DSL.
  - `gradle/libs.versions.toml` with the versions in [architecture](../architecture.md#stack).
  - JDK 21 toolchain.
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
- **Profiles:** a `local` profile with template and resource caching off.
- **Error pages:** `templates/error/404.html` and `5xx.html`.
- **Repository files:** `.gitignore`, a `README.md` explaining how to run, and the Commands section of `CLAUDE.md`.

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
- [ ] `./gradlew bootRun` from a clean clone, with Docker not running and no Node installed, serves the home page.
- [ ] The home page renders Shadleaf components, and the theme toggle switches between light and dark.
- [ ] The status card shows the Postgres version. It is filled by an htmx request to `/status`.
- [ ] `GET /status` without htmx headers returns the full page.
- [ ] A row written to `sync_state` survives an app restart.
- [ ] `./gradlew build` passes, including tests.
- [ ] The resolved classpath has no `graphql-java` artifact (`./gradlew :app:dependencies`).
- [ ] No template uses the Thymeleaf Layout Dialect, and no page loads a script from a CDN.

## Tests

- **`@WebMvcTest` with HtmlUnit:** `/` renders the status placeholder, found by `id`. `/status` returns the fragment
  with htmx headers and the full page without them.
- **`@SpringBootTest`:** the context starts with embedded Postgres, and the Flyway migration has applied.

## Open questions

- The port: 8080?

## Verification

—
