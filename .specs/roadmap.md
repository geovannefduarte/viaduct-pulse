# Roadmap

One slice is in progress at a time. Statuses are defined in [README](README.md#slice-lifecycle).

| Slice | Title | Chapter | Status | Spec |
|---|---|---|---|---|
| S00 | Skeleton: Gradle, Spring Boot 4.1 with Kotlin 2.2, embedded Postgres, Flyway, Shadleaf and htmx home page | — | in-progress | [S00](slices/S00-skeleton.md) |
| S01 | pulse-sync v1: commits and contributors, incremental, seed; plain-SQL pages | 1 | planned | — |
| S02 | Embed Viaduct: one module, Spring injector factory, `/graphql`, GraphiQL, guided-page frame | 2 | planned | — |
| S03 | Compatibility canary CI: pinned, latest and snapshot Viaduct versions; automated update PRs | — | planned | — |
| S04 | Generated code; `Commit` and `Contributor` as nodes with global IDs | 3 | planned | — |
| S05 | Field resolvers with declared inputs; query cards | 4 | planned | — |
| S06 | N+1, then batch resolvers, with the SQL tab | 5 | planned | — |
| S07 | pulse-sync v2 (pull requests, releases); connections | 6 | planned | — |
| S08 | pulse-sync v3 (CI runs, jobs); split into `code`, `people` and `ci` tenants | 7 | planned | — |
| S09 | Mutations: annotations on runs and jobs | 8 | planned | — |
| S10 | Viewers as request context; per-tenant dependency injection | 9 | planned | — |
| S11 | Public and maintainer schemas | 10 | planned | — |
| S12 | Actuator and Micrometer timings in the UI; error reporting | 11 | planned | — |
| S13 | Upgrade to Viaduct 3.0, and to the latest Spring Boot and Kotlin it allows | 12 | planned, waits for 3.0 | — |

## Later

These are ideas, not commitments:
- htmx 4 with `htmx-spring-boot` 6;
- a Java tenant module;
- comparing Viaduct's two execution engines;
- a chapter comparing Viaduct with database-generated GraphQL (pg_graphql).
