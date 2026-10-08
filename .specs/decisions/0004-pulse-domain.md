# 0004: Pulse as the domain

- **Status:** accepted, 2026-10-07

## Context

The candidates were:
- analytics on the Viaduct GitHub repository (Pulse);
- a Pokédex;
- a conference planner.

The domain has to exercise tenants, pagination at real scale, batching, cross-tenant reads, scopes and mutations.

## Decision

Pulse, reading the public `airbnb/viaduct` repository ([domain](../domain.md)).

## Consequences

- **Natural tenants:** `code`, `people` and `ci`.
- **Real scale:** thousands of commits and CI runs give pagination and batching a real workload.
- **A sync pipeline is needed** ([data sync](../data-sync.md)), and a committed seed keeps the app runnable without
  a token.
- **Only public data is stored.**
