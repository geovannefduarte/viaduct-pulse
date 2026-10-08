# UI guidelines

These are the rules for templates, controllers and frontend code. They follow Wim Deblauwe's guidance
([ADR 0007](decisions/0007-ui-follows-wim-deblauwe-with-shadleaf.md)), restated in our own words for this stack.

Each rule cites its source:

| Code | Source |
|---|---|
| **TT** | *Taming Thymeleaf*, 4.0.0, by chapter and section |
| **MFH** | *Modern frontends with htmx*, 1.1.1, by chapter and section |
| **SL** | Shadleaf documentation |
| **Blog** | a post listed in [references](references.md#blog-posts) |

A rule marked **adapted** changes the source's rule to fit this stack and says how. A rule marked **ours** has no
source. When a newer source contradicts an older one, the newer one wins and the rule says so.

## Assets and tooling

- **The toolchain is the JDK and Gradle only.** There is no Node, Vite or Tailwind build. **Adapted:** TT §4.2
  builds the frontend with npm and Vite.
- **JavaScript comes from webjars through `webjars-locator-lite`,** linked without versions:
  `@{/webjars/htmx.org/dist/htmx.min.js}`. No CDN. (TT ch13 §13.1)
- **Shadleaf serves its own CSS and Alpine.js.** Add its `theme-script` and `assets` fragments to `<head>`, and don't
  load Alpine separately. (SL Getting Started)
- **Page layout CSS is one small stylesheet that uses Shadleaf's design tokens.** No utility framework, and no colors
  hard-coded outside the tokens. **Adapted:** TT §4.2.4 defines its tokens in Tailwind's `@theme`.
- **A `local` profile turns off template and resource caching,** so edits show on refresh. (TT §4.2.3)
- **Message bundles need a restart;** they are not reloaded. (TT ch8 §8.3)

## Layouts

- **Build the page shell from a parameterized fragment, not the Layout Dialect.** A page calls
  `th:replace="~{layout/main :: layout(title, ~{::#page-content})}"`. Blog 2026-02-25 and Shadleaf's samples both
  replace TT ch6's Layout Dialect advice.
- **The page sets which menu item is active** and the menu fragment compares against it. (TT ch7 §7.6)

## Fragments

- **Each UI region is one parameterized fragment,** used both for the full page and for an htmx swap, so the two
  can't drift. (TT §16.6.4, MFH §5.3.1)
- **Put `th:fragment` on the component's root element** and include it with `th:replace`. (TT §5.2)
- **Always declare a fragment's parameters.** Optional ones get a default via `th:with="x=${x?:default}"`.
  (TT §5.3, ch10 §10.3)
- **Don't name a fragment after an HTML tag.** (TT §10.3)
- **Prefer a Shadleaf component to a hand-written one.** To change a component, copy its template to
  `templates/sl/components/` and keep the copy minimal. (SL)
- **A fragment rendered alone must receive its whole model,** including values its page loop would have provided.
  (MFH §6.2.5)

## Controllers

- **Controllers stay thin and delegate to services.** There is one controller per path section, in a `web`
  sub-package of the feature. (TT ch7 §7.1, §7.6)
- **URLs follow one scheme:** `GET /runs`, `GET /runs/{id}`, `GET|POST /runs/{id}/annotations`. (TT ch11–13)
- **After a successful form POST, redirect** (Post/Redirect/Get), with a flash message where useful. (TT §7.4, §13.3)
- **Typed IDs bind through a `Converter`.** Shared reference data comes from `@ModelAttribute` methods, and app-wide
  values such as the current viewer come from a `@ControllerAdvice`. (TT §12.2, §16.3)
- **Use URL parameters for lists:** page, size, sort and filters. Paging always has an explicit sort. (TT §10.4)

## htmx

- **htmx endpoints return HTML fragments, never JSON.** (MFH ch3)
- **Every htmx endpoint also has a full-page version for the same URL.** `@HxRequest` ignores history-restore
  requests by default, so the full page must exist. (MFH §5.3.1; `htmx-spring-boot` 5.x behavior)
- **Choose fragment vs page by what the request targets,** with `@HxRequest(target = …)` or `triggerId`, not by
  `HX-Request` alone. A request that follows an htmx redirect still carries `HX-Request`. (MFH §9.10.1)
- **Use the `hx:` dialect from `htmx-spring-boot-thymeleaf`** for attributes that need Thymeleaf expressions:
  `hx:get="@{…}"`. (MFH §3.1, §4.3)
- **Prefer relative targets** (`closest`, `find`, `this`) to ids that must be unique on the page. (MFH §3.3)
- **Validation errors come back as status 200 with the form fragment.** htmx 2 does not swap 4xx responses by
  default. (MFH §7.2.3.2, SL htmx guide)
- **Removing an element means returning an empty 200 response.** A 204 response is never swapped. Redirect after a
  DELETE with 303, not 302. (MFH §5.3.4, §9.6)
- **Use response headers for cross-cutting effects:** `HtmxResponse.addTrigger(…)` for events that other parts of
  the page listen to, and `redirect:htmx:/…` or `refresh:htmx` for navigation. Shadleaf's `sl-toast`,
  `sl-dialog-close` and `sl-popover-close` are such events. (`htmx-spring-boot` 5.2.0, SL)
- **Several fragments in one response use Spring's `FragmentsRendering`** with `hx-swap-oob` on each extra root.
  The book's `HtmxResponse.builder().view(…)` no longer exists. (MFH ch6, adapted)
- **Debounce search and filter inputs** (`keyup changed delay:300ms, search`). Push the URL with `hx-push-url` so a
  reload or a shared link reproduces the state. (MFH §3.2.1, §9.9.2)
- **Put the default `hx-target` and `hx-swap` on a container,** not repeated on each child. (MFH §9.11.2)
- **Set a global request timeout** in the `htmx-config` meta tag, and show request errors through one document-level
  handler. (MFH §7.1.5)
- **Long-running work starts with polling** that stops itself when the work finishes. Move to SSE (`SseEmitter`,
  named events, a 5-second heartbeat) only when streamed output is needed. **Adapted:** MFH §11.1 uses WebFlux.
  (MFH §9.11, §11.1)

## Client-side behavior

- **Use Alpine for local UI state only:** toggles, tabs, an Escape key to cancel. Use plain JavaScript for global
  concerns such as the request-error handler. Shadleaf's components already carry their own Alpine behavior.
  (MFH ch7)
- **Escape server data embedded in Alpine attributes.** (TT §13.1)

## Forms

- **Form objects are separate from domain objects:** a mutable `…FormData`, then an immutable `…Parameters` via
  `toParameters()`. Never bind domain objects to forms. (TT §10.1, §16.5.1)
- **Always validate on the server** with `@Valid` and a `BindingResult` immediately after it. On errors, re-render;
  on success, redirect or return the success fragment. (TT §11.1, MFH §7.2.3.2)
- **Kotlin form objects use `var` properties and `@field:` annotation targets** so Bean Validation sees them.
  **Ours** (unverified; S09 proves it).
- **Trim all string input globally** with `StringTrimmerEditor` in the `@ControllerAdvice`. (TT §16.2)

## Tables and pagination

- **Build tables from Shadleaf's table component,** and wrap it in our own fragments only for repeated column
  patterns. (SL; TT §10.3)
- **Paginate in SQL** (`LIMIT`/`OFFSET` plus a count), not in Kotlin. Return a Spring Data `Page` so one pagination
  fragment serves every list. (MFH §9.8.2, TT §10.4) **Adapted:** TT uses JPA repositories.
- **Build pagination links from a request-scoped URL builder,** computed in `th:with`, because `th:href` runs in a
  restricted mode. (TT §10.4)

## Text, errors, viewers

- **All user-facing text lives in message bundles from the first slice.** Translate whole sentences with
  placeholders. (TT ch8, §10.4)
- **Error pages per status code** live in `templates/error/`. Stack traces are shown only under the `local`
  profile. (TT §12.5)
- **The simulated viewer is an app-wide model attribute.** Templates hide what the viewer can't use, and the server
  enforces it too. **Adapted:** TT ch14 uses Spring Security's `sec:authorize`.

## Testing

- **Controller tests use `@WebMvcTest` with HtmlUnit,** asserting on elements found by `id`, not on strings. (TT
  ch15 §15.1.3)
- **htmx endpoints are tested both ways:** with the `HX-Request` and target headers for the fragment, and without
  them for the full page. **Ours:** MFH has no tests.
- **Data-access tests run against real Postgres.** **Adapted:** TT §9.3.4 uses Testcontainers; we use embedded
  Postgres.
- **Test data comes from Object Mother classes,** for example `Commits.aCommit()`. (TT ch15)
- **No Cypress.** It needs Node and Docker. A browser-level test tool is an open question for later slices.
