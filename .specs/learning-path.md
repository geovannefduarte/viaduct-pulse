# Learning path

Each chapter is one slice's guided page in the running app and one git tag, `chNN`. A reader can check out a tag,
run the app, and diff against the previous tag to see exactly what the chapter added.

## Chapters

The tutorial numbers refer to Viaduct's runnable tutorials in `core/tenant/tutorials` in airbnb/viaduct. Each
chapter links the matching tutorial and doc page.

| Ch | Slice | Concept | Viaduct tutorial |
|---|---|---|---|
| 1 | S01 | The problem: plain SQL pages, over- and under-fetching | — |
| 2 | S02 | Embedding Viaduct: settings plugin, one module, a field resolver, GraphiQL | 01 |
| 3 | S04 | Schema-first: generated types and resolver bases; nodes and global IDs | 02 |
| 4 | S05 | Field resolvers with declared inputs (required selection sets) | 03, 04 |
| 5 | S06 | N+1, then batch resolvers | 07, 08 |
| 6 | S07 | Connections and pagination | 12 |
| 7 | S08 | Multi-tenancy: subqueries, root field references, named fragments, operations | 11, 13, 14, 15 |
| 8 | S09 | Mutations | 05 |
| 9 | S10 | Request context and per-tenant dependency injection | — |
| 10 | S11 | Scoped schemas: public vs maintainer | 06 |
| 11 | S12 | Observability: metrics and error reporting | — |
| 12 | S13 | Upgrading to Viaduct 3.0 | — |

Slices S00 and S03 add no chapter.

## Guided page

Each chapter page has the same parts:
1. A short explanation, two or three sentences.
2. The code that implements the concept, read from this repository's source at build time so the page cannot drift
   from the code. The mechanism is decided in S02.
3. One or more query cards.
4. A link to the matching Viaduct doc and tutorial.

## Query card

A query card is a Shadleaf card holding an editable GraphQL query and a Run button. It is a form that posts with
htmx. The server answers with a fragment holding three Shadleaf tabs:

- **Result:** the JSON response.
- **SQL:** the statements this request ran, in order. This makes the N+1 chapter visible.
- **Timings:** per-field timings from Viaduct's Micrometer timers, available from S12. Earlier chapters show total
  time only.

Validation and GraphQL errors come back as the same fragment with status 200
([UI guidelines](ui-guidelines.md#htmx)). Cards use relative targets, so a page can hold any number of them.

A separate free-exploration page embeds GraphiQL, served as a webjar.
