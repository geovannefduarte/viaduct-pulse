# Upstream findings

These are bugs, doc drift, API gaps and contribution ideas for the open-source projects Pulse builds on.
- Each entry says whether it is verified, and how.
- Before filing an issue or opening a PR, re-check the entry against the project's main branch.
- Once something is filed, move it to **Filed** with a link.

## Viaduct (airbnb/viaduct)

| Finding | Location | Evidence |
|---|---|---|
| The bundled GraphiQL page imports `/js/introspection-patch.js`, but no such file exists in the repository | `core/service/wiring/src/main/resources/graphiql/index.html:80` | verified by file listing; the effect in a browser is untested |
| The "from scratch" guide pins `viaduct = "0.7.0"` and never applies the settings plugin, which the module and application plugins require | `docs/docs/getting_started/setup/from_scratch/index.md:51` | verified by grep (0 mentions of `settings-gradle-plugin`); not run |
| Two pages say the Star Wars demo runs on Spring Boot; it uses Micronaut | `docs/docs/getting_started/setup/clone/index.md:50`, `docs/docs/getting_started/starwars/architecture/index.md:9` | verified by reading both pages |
| The feature flags page lists `ENABLE_SYNC_VALUE_COMPUTATION`, which no longer exists in `core/` | `docs/docs/docs/service_engineers/feature_flags/index.md:19` | verified by grep (0 hits in `core/`) |
| No public way to add resolver instrumentation, observe DataLoader batch sizes or read the query plan | `ViaductBuilder` vs `StandardViaduct.Builder` | verified by reading the source; affects S06 and S12 |
| No demo app integrates with Spring; `demoapps/spring-starter` is a placeholder without Spring | `demoapps/spring-starter/README.md` | verified by reading; Pulse could become that example |

## Shadleaf (wimdeblauwe/shadleaf)

| Finding | Location | Evidence |
|---|---|---|
| The README points contributors to `CLAUDE.md` for the live-reload loop, but `.gitignore` excludes that file | `README.md:57`, `.gitignore` | verified at a clone made 2026-10-08 |
| The docs and samples show only Maven and Java; there is no Gradle Kotlin DSL setup and no Kotlin example | `docs/`, `samples/` | verified by grep (0 files mention gradle or kotlin) |
| Getting Started says "Spring Boot 4", but the compatibility table lists only 4.1.x | `docs/src/content/docs/getting-started.mdx:8`, `README.md:36-42` | verified by reading both |
| Spring Boot 4.0.x compatibility is untested; Boot 4.0.8 and 4.1.1 manage the same Spring Framework and Thymeleaf | — | managed versions verified on Maven Central; compatibility itself untested |
| The htmx guide pins `htmx-spring-boot-thymeleaf` 5.1.1; 5.2.0 is the current 5.x release | `guides/htmx` page, "Loading htmx" | verified on the published docs and Maven Central, 2026-10-08 |

## htmx-spring-boot (wimdeblauwe/htmx-spring-boot)

| Finding | Location | Evidence |
|---|---|---|
| The README on `main` still links `@HxTriggerAfterSettle` and `@HxTriggerAfterSwap`, which 6.0.0 removed | `README.md:160-161` | verified: the README lines exist and no class file by those names exists on `main` at `6c70b03` |
| No Kotlin examples | `README.md` | from the research notes; re-check before filing |

## Filed

None yet.
