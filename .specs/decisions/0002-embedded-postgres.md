# 0002: Embedded Postgres, plain SQL

- **Status:** accepted, 2026-10-07. Corrected 2026-10-08: the Postgres binaries version (S00).

## Context

- The app must run with one command, without Docker.
- H2 in PostgreSQL mode needs no setup, but it only emulates Postgres.
- SQLite does not speak Postgres SQL.
- The N+1 and batching chapters need every SQL statement a request runs to be visible.

## Decision

- Run a real Postgres through `io.zonky.test:embedded-postgres` 2.2.2.
- Use Postgres 18.6 by importing `io.zonky.test.postgres:embedded-postgres-binaries-bom` 18.6.0 and adding the
  `darwin-arm64v8` and `linux-arm64v8` binaries. On its own, `embedded-postgres` 2.2.2 pulls Postgres 14.22.0
  binaries for x86 platforms only (its POM, checked in S00).
- Manage the schema with Flyway.
- Access data with Spring's `JdbcClient` and hand-written SQL. No ORM.

## Consequences

- The database behaves like production Postgres.
- Each platform downloads a binaries artifact from Maven Central on first build.
- Embedded Postgres has one owner per data directory, which shapes how sync runs
  ([data sync](../data-sync.md#open-questions)).
- Postgres extensions such as pg_graphql are not available. **Unverified:** whether zonky can load extensions.
