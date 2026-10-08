# 0007: The UI follows Wim Deblauwe's approach, with Shadleaf

- **Status:** accepted, 2026-10-08. Supersedes [0003](0003-thymeleaf-and-htmx.md).

## Context

- The UI should follow Wim Deblauwe's guidance on Thymeleaf and htmx: his books *Taming Thymeleaf* and *Modern
  frontends with htmx*, plus his blog ([references](../references.md)).
- The project wants shadcn/ui-style components without writing React.
- Shadleaf brings shadcn/ui components to Thymeleaf as a Spring Boot starter and a Thymeleaf dialect. Its
  Getting Started page says it needs no Node.js and no Tailwind.
  - The compiled CSS and Alpine.js ship inside the jar.
  - It is new: version 0.7.0, first released 2026-09-29.
- The 4th edition of *Taming Thymeleaf* builds its frontend with npm, Vite and Tailwind. That conflicts with this
  project's one-command, no-Node principle ([vision](../vision.md#principles)).

## Decision

- Use Thymeleaf, htmx 2.0.11 with `htmx-spring-boot-thymeleaf` 5.2.0, and Shadleaf 0.7.0 for components.
- Follow [UI guidelines](../ui-guidelines.md). They adapt Wim Deblauwe's rules to this stack and cite where each
  rule comes from.
- Serve every JavaScript library as a webjar through `webjars-locator-lite`: htmx, and GraphiQL on the exploration
  page. No CDN.
- Use no Node toolchain and no Tailwind build. Page layout uses a small stylesheet built on Shadleaf's design tokens.
- Use no Thymeleaf Layout Dialect. Wim Deblauwe's post of 2026-02-25 recommends moving away from it, and Shadleaf's
  samples use parameterized `th:replace` layouts instead.

## Consequences

- One command still runs the app. The JDK and Gradle are the whole toolchain.
- Shadleaf is pre-1.0, so its markup may change between releases. Upgrades get their own slice, and any problems
  found are reported upstream ([upstream findings](../upstream-findings.md)).
- `htmx-spring-boot` 6.0.0, released 2026-10-08, targets htmx 4 and Spring Boot 4.1. Moving to it is a separate
  slice.
- Where the books and this stack differ (Gradle instead of Maven, Kotlin instead of Java, JdbcClient instead of JPA,
  embedded Postgres instead of Testcontainers, no Cypress), the guidelines adapt the rule and say so.
