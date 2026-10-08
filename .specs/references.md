# References

These are the outside sources the specs rely on. Last reviewed: 2026-10-08.

## Keeping this current

- **At the start of each slice,** check the blog feed and the releases of the projects below. Add new relevant posts
  or releases here, with their date.
- **When a source changes a rule,** update [UI guidelines](ui-guidelines.md) and cite the new source.
- **The books are paid works.** Specs cite them by chapter and section and restate their rules in our own words.
  Never copy their text or code listings into this repository.

## Books by Wim Deblauwe

| Book | Edition | Targets |
|---|---|---|
| *Taming Thymeleaf* | 4.0.0, 2026-09-27 | Spring Boot 4.1.1, Tailwind 4 built with Vite, Alpine.js, Testcontainers, Cypress |
| *Modern frontends with htmx* | 1.1.1, 2024-10-01 | Spring Boot 3.3.1, htmx 2.0.0, htmx-spring-boot 3.4.1 |

## Projects

| Project | Version used | Notes |
|---|---|---|
| [Shadleaf](https://github.com/wimdeblauwe/shadleaf) ([docs](https://wimdeblauwe.github.io/shadleaf/current/)) | 0.7.0, 2026-10-02 | Spring Boot 4.1.x; no Node or Tailwind needed; bundles Alpine |
| [htmx-spring-boot](https://github.com/wimdeblauwe/htmx-spring-boot) | 5.2.0, 2026-10-02 | 5.x supports htmx 1 and 2. 6.0.0 (2026-10-08) supports htmx 4 only and needs Spring Boot 4.1 |
| [ttcli](https://github.com/wimdeblauwe/ttcli) | not used | His project generator: Maven and Java only, Vite by default. It shows his current default setup |
| [htmx](https://htmx.org) | 2.0.11 | npm `latest` tag; 4.0.0 is under the `next` tag |

## Blog

- **Feed:** <https://www.wimdeblauwe.com/blog/index.xml>. The whole-site feed is <https://www.wimdeblauwe.com/index.xml>.

### Blog posts

These are the posts relevant to this project, newest first.

| Date | Post | Why it matters here |
|---|---|---|
| 2026-09-27 | [Taming Thymeleaf 4th edition release](https://www.wimdeblauwe.com/blog/2026/09/27/taming-thymeleaf-4th-edition-release/) | What the 4th edition changed |
| 2026-09-06 | [Testing your auto-configuration against a missing optional dependency](https://www.wimdeblauwe.com/blog/2026/09/06/testing-your-auto-configuration-against-a-missing-optional-dependency/) | Testing technique |
| 2026-07-27 to 08-17 | Writing a Thymeleaf component library, [part 1](https://www.wimdeblauwe.com/blog/2026/07/27/writing-a-thymeleaf-component-library/), [2](https://www.wimdeblauwe.com/blog/2026/08/03/writing-a-thymeleaf-component-library---part-2/), [3](https://www.wimdeblauwe.com/blog/2026/08/10/writing-a-thymeleaf-component-library---part-3/), [4](https://www.wimdeblauwe.com/blog/2026/08/17/writing-a-thymeleaf-component-library---part-4/) | The design behind Shadleaf |
| 2026-02-25 | [Migrating away from Thymeleaf Layout Dialect](https://www.wimdeblauwe.com/blog/2026/02/25/migrating-away-from-thymeleaf-layout-dialect/) | Replaces TT ch6's layout advice |
| 2025-09-08 | [How I document production-ready Spring Boot applications](https://www.wimdeblauwe.com/blog/2025/09/08/how-i-document-production-ready-spring-boot-applications/) | Documentation practice |
| 2025-07-30 | [How I test production-ready Spring Boot applications](https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/) | Testing practice |
| 2025-06-24 | [How I write production-ready Spring Boot applications](https://www.wimdeblauwe.com/blog/2025/06/24/how-i-write-production-ready-spring-boot-applications/) | Packages and naming |
| 2024-12-31 | [Problems I no longer have by using server-side rendering](https://www.wimdeblauwe.com/blog/2024/12/31/problems-i-no-longer-have-by-using-server-side-rendering/) | The case for server rendering |
| 2024-11-19 | [Redirect attributes with Spring MVC and htmx](https://www.wimdeblauwe.com/blog/2024/11/19/redirect-attributes-with-spring-mvc-and-htmx/) | Flash messages after htmx redirects |
| 2023-12-14 | [Htmx global error handler](https://www.wimdeblauwe.com/blog/2023/12/14/htmx-global-error-handler/) | Error handling. Uses an older `htmx-spring-boot` API |
| 2023-10-19 | [Taming Thymeleaf update for Thymeleaf 3.1.2](https://www.wimdeblauwe.com/blog/2023/10/19/taming-thymeleaf-update-for-thymeleaf-3.1.2/) | Why the pagination URL builder must be a bean |

## Viaduct

- Source: <https://github.com/airbnb/viaduct>
- Documentation: <https://viaduct.airbnb.tech>
