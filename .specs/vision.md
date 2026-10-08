# Vision

## Goals

1. **Learn Viaduct as a whole:** what problems it solves, how a request executes, how tenants compose. The vehicle
   is a real application, not a toy.
2. **Give others the same path:** clone, run, follow the chapters. Each chapter is a git tag plus a guided page in
   the running app.
3. **Be a compatibility canary:** a real Viaduct application whose tests run against each new Viaduct release, and
   against snapshots, before the app upgrades. It catches breaking API changes and publishing problems.

## Audience

- **Primary:** the author, learning Viaduct from the basics.
- **Secondary:** developers evaluating or learning Viaduct. A fresh clone gives them the same experience.

## Principles

- **One command to run:** `./gradlew bootRun`. No Docker, no GitHub token, no Node toolchain. A seed snapshot ships
  with the repository.
- **Teach by doing.** Each concept is a live query, the code behind it, and what Viaduct did to answer it: the
  result, the SQL it ran and per-field timings.
- **Show the problem before the fix:** plain SQL pages before GraphQL, N+1 before batching.
- **Build in slices.** Each slice runs, has tests and adds at most one chapter.
- **Public Viaduct API only** ([ADR 0005](decisions/0005-public-viaduct-api-only.md)).
- **Real data at real scale:** the `airbnb/viaduct` repository's own history.

## Non-goals

- A general GitHub analytics product. Pulse reads one configured repository.
- A JavaScript single-page app or a frontend build step. GraphiQL from a CDN is the one widget exception
  ([ADR 0003](decisions/0003-thymeleaf-and-htmx.md)).
- Production deployment or real authentication. Viewers are simulated for teaching ([domain](domain.md#viewers)).
- Replacing Viaduct's documentation. Chapters link to it.
