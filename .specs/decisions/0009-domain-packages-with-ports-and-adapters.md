# 0009: Domain packages with ports and adapters

- **Status:** accepted, 2026-10-08

## Context

- The code should show where each piece belongs, so a reader can find the Viaduct parts quickly and see what they
  depend on.
- The domain has natural boundaries: code, people and CI ([domain](../domain.md)). S08 splits the Viaduct schema into
  tenants along the same lines.
- *Taming Thymeleaf* (ch7 §7.1) organizes code by feature, with a `web` sub-package for controllers.
- Full Clean Architecture adds a use-case class per operation, presenters and a mapping layer at every boundary. In an
  app this size that ceremony would hide the Viaduct code the project exists to teach.
- Spring Modulith 2.1.1 is built against Spring Boot 4.1.1 and Spring Framework 7.0.9, the versions this project uses
  (its POMs, checked on Maven Central, 2026-10-08). It verifies module boundaries with ArchUnit 1.4.2.

## Decision

- **One top-level package per domain** under `dev.geovanne.pulse`, such as `system` and later `code`, `people` and
  `ci`. Spring Modulith treats each as an application module.
- **Inside a domain, four layers:**
  - `domain`: the model and the ports (interfaces) the domain needs. Plain Kotlin, no Spring and no SQL.
  - `application`: services that carry out what the adapters ask for, through the ports.
  - `web`: Spring MVC controllers, which call `application`.
  - `persistence`: implementations of the ports, with `JdbcClient` and SQL.
- **Dependencies point inward:** `web` and `persistence` depend on `application` and `domain`; `application` depends on
  `domain`; `domain` depends on nothing in the app.
- **A layer is added only when it has code.** A domain without storage has no `persistence` package.
- **Cross-cutting setup lives in `platform`**, such as embedded Postgres. Domains don't call it; they receive what it
  provides through Spring.
- **Tests enforce the rules:** `ApplicationModules.verify()` from Spring Modulith, plus ArchUnit rules for the layer
  directions.
- **Other domains see only a domain's base package.** That is Spring Modulith's default, and every layer lives in a
  sub-package, so today no domain can call another. The first slice with a cross-domain call decides how a domain
  exposes its `application` services, for example `@NamedInterface` or a facade in the base package, and records it
  here.
- **Viaduct resolvers will be adapters too.** S02 decides how tenant modules reach a domain's `application` layer.

## Consequences

- A domain reads top to bottom: what it is (`domain`), what it does (`application`), and how it is reached or stored
  (`web`, `persistence`).
- A small domain has a one-line application service. This is accepted, so every domain has the same shape.
- A boundary violation fails `./gradlew build`, not a review.
- Spring Modulith is a test dependency only. It does not change how the app runs.
