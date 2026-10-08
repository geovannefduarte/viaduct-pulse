# viaduct-pulse

A Spring Boot application that serves analytics about the public `airbnb/viaduct` GitHub repository through
[Viaduct](https://github.com/airbnb/viaduct), an open-source GraphQL server. It has three jobs:

1. Learn Viaduct end to end by building a real application with it, one slice at a time.
2. Let others learn the same way: each slice is a tagged chapter with a guided page in the running app.
3. Act as a compatibility canary: its tests run against each new Viaduct release before the app upgrades.

## Start here

Plans live in `.specs/`. Before changing code:

1. Read `.specs/README.md` for how specs work.
2. Find the active slice in `.specs/roadmap.md` and read its spec in `.specs/slices/`.
3. Read only the cross-cutting specs that the slice spec links.

Work stays inside the active slice's scope. If the work needs something its spec doesn't cover, update the spec
first, then the code.

## Rules

- **This repository is public.** Reference Viaduct only through public sources: github.com/airbnb/viaduct,
  viaduct.airbnb.tech, Maven Central and the Gradle Plugin Portal.
- **Public GitHub data only.** Store logins, display names and avatar URLs. Never store commit or account emails.
- **Public Viaduct API only.** If a feature needs an internal Viaduct class, record it in
  `.specs/upstream-findings.md` instead of using it ([ADR 0005](.specs/decisions/0005-public-viaduct-api-only.md)).
- **No second graphql-java.** Never add `spring-boot-starter-graphql` or graphql-java itself; Viaduct's runtime jar
  bundles it ([architecture](.specs/architecture.md#constraints)).
- **UI code follows [`.specs/ui-guidelines.md`](.specs/ui-guidelines.md).** It adapts Wim Deblauwe's Thymeleaf
  and htmx guidance to this stack: Shadleaf components, htmx, webjars, no Node toolchain, no Layout Dialect.
  Before writing templates or controllers, read the sections that apply.
- **Never copy text or code from the books listed in [`.specs/references.md`](.specs/references.md).** Cite them by
  chapter and section instead.
- **Every slice ships tests.** Every claim in a spec is either verified, with how, or marked unverified.
- **Versions are pinned** in `gradle/libs.versions.toml`. Bump Spring Boot, Kotlin or Viaduct only in a slice that
  plans it.

## Commands

Run these from the repository root. `make` lists every target. Make takes `JAVA_HOME` from asdf when asdf has a Java
version for this folder (`.tool-versions`).

| Command | Does |
|---|---|
| `make run` | Starts the app on http://localhost:8080 with the `local` profile; data lives in `.pulse/pgdata` |
| `make test` | Runs every test, including the architecture rules |
| `make build` | Compiles, runs every test and checks formatting |
| `make format` | Formats Kotlin sources and Gradle scripts with ktlint |
| `make db-reset` | Deletes the local database; stop the app first |

Each target wraps `./gradlew`, which works directly too, for example `./gradlew :app:test --tests '*StatusControllerTest'`.
