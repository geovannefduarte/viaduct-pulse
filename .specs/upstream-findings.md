# Upstream findings

These are Viaduct bugs, doc drift and API gaps found while building Pulse. Each entry says whether it is verified
and how. Paths are in airbnb/viaduct. Before filing an issue upstream, re-check the entry against Viaduct's main
branch.

## Open

| Finding | Location | Evidence |
|---|---|---|
| The bundled GraphiQL page imports `/js/introspection-patch.js`, but no such file exists in the repository | `core/service/wiring/src/main/resources/graphiql/index.html:80` | verified by file listing; the effect in a browser is untested |
| The "from scratch" guide pins `viaduct = "0.7.0"` and never applies the settings plugin, which the module and application plugins require | `docs/docs/getting_started/setup/from_scratch/index.md:51` | verified by grep (0 mentions of `settings-gradle-plugin`); not run |
| Two pages say the Star Wars demo runs on Spring Boot; it uses Micronaut | `docs/docs/getting_started/setup/clone/index.md:50`, `docs/docs/getting_started/starwars/architecture/index.md:9` | verified by reading both pages |
| The feature flags page lists `ENABLE_SYNC_VALUE_COMPUTATION`, which no longer exists in `core/` | `docs/docs/docs/service_engineers/feature_flags/index.md:19` | verified by grep (0 hits in `core/`) |
| No public way to add resolver instrumentation, observe DataLoader batch sizes or read the query plan | `ViaductBuilder` vs `StandardViaduct.Builder` | verified by reading the source; affects S06 and S12 |

## Filed

None yet.
