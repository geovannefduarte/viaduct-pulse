# viaduct-pulse

Pulse is a small web application about the [airbnb/viaduct](https://github.com/airbnb/viaduct) repository: its
commits, contributors, pull requests, releases and CI runs. It is built on [Viaduct](https://viaduct.airbnb.tech),
an open-source GraphQL server, and exists to learn Viaduct one step at a time. The goal is for each step to be a
tagged chapter with a guided page in the running app; the first chapters are still being built.

It is a Kotlin Spring Boot application with server-rendered pages: Thymeleaf, htmx and
[Shadleaf](https://wimdeblauwe.github.io/shadleaf/current/) components. Its database is a real Postgres that runs
embedded in the app.

## Running it

You need a Java 25 JDK. With [asdf](https://asdf-vm.com), `make setup` installs the one pinned in `.tool-versions`.

```sh
make run
```

Then open http://localhost:8080. No Docker and no Node are needed. The first build downloads the Postgres binaries
from Maven Central. The database lives in `.pulse/pgdata` and survives restarts.

`make build` compiles the app, runs the tests and checks the formatting. `make` lists the other targets. Each one
wraps `./gradlew`, which works directly too.

## How the code is organized

Each domain is a package with up to four layers: `domain` (the model and its ports), `application` (services),
`web` (controllers) and `persistence` (SQL). Shared setup lives in `platform`. A test fails the build when one domain
reaches into another's internals, or when a layer depends in the wrong direction. See
[ADR 0009](.specs/decisions/0009-domain-packages-with-ports-and-adapters.md).

## Where the data comes from

Pulse will read only public metadata of the `airbnb/viaduct` GitHub repository, through git and the GitHub API. It
will store logins, display names and avatar URLs, and never email addresses. The first sync arrives in slice S01. Pulse is a personal project, not an
Airbnb project.

## License

[Apache License 2.0](LICENSE).
