# viaduct-pulse

Pulse is a small web application about the [airbnb/viaduct](https://github.com/airbnb/viaduct) repository: its
commits, contributors, pull requests, releases and CI runs. It is built on [Viaduct](https://viaduct.airbnb.tech),
an open-source GraphQL server, and exists to learn Viaduct one step at a time. Each step is a tagged chapter with a
guided page in the running app.

It is a Kotlin Spring Boot application with server-rendered pages: Thymeleaf, htmx and
[Shadleaf](https://wimdeblauwe.github.io/shadleaf/current/) components. Its database is a real Postgres that runs
embedded in the app.

## Running it

You need a Java 25 JDK. With [asdf](https://asdf-vm.com), `asdf install` in this folder installs the one pinned in
`.tool-versions`.

```sh
./gradlew bootRun
```

Then open http://localhost:8080. No Docker and no Node are needed. The first build downloads the Postgres binaries
from Maven Central. The database lives in `.pulse/pgdata` and survives restarts.

`./gradlew build` compiles the app, runs the tests and checks the formatting.

## Where the data comes from

Pulse reads only public metadata of the `airbnb/viaduct` GitHub repository, through git and the GitHub API. It
stores logins, display names and avatar URLs, and never stores email addresses. Pulse is a personal project, not an
Airbnb project.

## License

[Apache License 2.0](LICENSE).
