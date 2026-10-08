# Specs

This folder plans viaduct-pulse. Agents and humans read it before writing code.

## Layout

| Path | Holds | Changes when |
|---|---|---|
| `vision.md` | Goals, audience, principles, non-goals | rarely |
| `architecture.md` | Stack, pinned versions, constraints | a decision changes the stack |
| `domain.md` | Pulse entities, tenants, viewers, schema sketch | a slice adds entities |
| `data-sync.md` | How `pulse-sync` fetches GitHub data incrementally | a sync slice runs |
| `learning-path.md` | Chapters and the guided-page experience | a chapter is added |
| `roadmap.md` | Every slice and its status | every slice |
| `slices/` | One spec per slice, from `_template.md` | while the slice is active |
| `decisions/` | One ADR per decision | a decision is made |
| `upstream-findings.md` | Viaduct bugs, doc drift and API gaps found while building | a finding is verified |

## Slice lifecycle

`planned` → `draft` → `ready` → `in-progress` → `done`

- **planned:** a one-line row in `roadmap.md`, no spec file yet.
- **draft:** the spec exists; open questions remain.
- **ready:** every open question is answered and every acceptance criterion is checkable. Only a ready slice is
  implemented.
- **in-progress:** one slice at a time.
- **done:** every acceptance criterion is verified, with evidence in the spec's Verification section, and the
  git tag exists.

Write a slice's spec when the slice before it is done, not earlier. Later slices stay one-line rows so they are
planned against the code that exists.

## Decisions

An ADR is `decisions/NNNN-short-title.md` with Status, Context, Decision and Consequences. An accepted ADR is not
edited. A new ADR supersedes it, and the old one's Status links the new one.

## Writing rules

- Mark each nontrivial claim as verified, saying how, or as unverified.
- Link instead of repeating. A slice spec links `architecture.md` instead of copying versions.
- Keep this repository's public-only rule (`CLAUDE.md`) in every spec.
