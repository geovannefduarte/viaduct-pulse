# 0003: Thymeleaf and htmx, no JavaScript framework

- **Status:** accepted, 2026-10-07

## Context

The app needs a good interactive UI: editable queries, swapped-in results, tabs. React or Vue would add a frontend
toolchain that this project doesn't want.

## Decision

- Render HTML on the server with Thymeleaf. Add interactivity with htmx 2.0.11, through `htmx-spring-boot-thymeleaf`
  5.2.0.
- GraphiQL, which is a React widget, is loaded from a CDN on the free-exploration page only. It is self-contained,
  and the project writes no React code.

## Consequences

- There is no Node toolchain or frontend build step.
- Query cards round-trip through the server, which also makes the server-side SQL and timings available to render.
- htmx 4.0.0 exists under npm's `next` tag. Moving to it is a separate decision.
