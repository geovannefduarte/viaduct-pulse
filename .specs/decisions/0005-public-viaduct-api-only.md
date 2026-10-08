# 0005: Public Viaduct API only

- **Status:** accepted, 2026-10-07

## Context

- Pulse is a compatibility canary, so it should break only where a real Viaduct user would break.
- Some internals a learner wants to see have no public hook in Viaduct's main branch: the query plan, DataLoader batch
  sizes and per-resolver instrumentation. Per-resolver instrumentation exists only on an internal builder.

## Decision

- Use only Viaduct's public API.
- Show internals through what is public:
  - the generated module configuration under `META-INF/viaduct/modules/`;
  - Micrometer timers;
  - the SQL each request runs.
- When a chapter needs more, record the gap in [upstream findings](../upstream-findings.md) as a candidate for a
  public API proposal.

## Consequences

- An upgrade that breaks Pulse would break any outside user too.
- Some engine internals stay invisible until Viaduct offers a public hook.
