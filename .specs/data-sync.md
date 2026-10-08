# Data sync: pulse-sync

`pulse-sync` fills Postgres from a local git clone and the GitHub API. After the first run it fetches only what
changed. It is written in Kotlin in the `:sync` project, so the repository has one toolchain.

## Rules

- **Incremental.** Each source keeps a watermark in a `sync_state` table. A run reads from the watermark forward.
- **Idempotent.** Every write is an upsert (`INSERT … ON CONFLICT … DO UPDATE`). Re-running a sync is safe.
- **Crash-safe.** A batch's rows and its watermark update commit in one transaction, so the watermark never moves
  past data that wasn't saved.
- **Cheap on the API.** Requests are conditional, using ETags. **Unverified here, from GitHub's REST docs:** a 304
  response does not count against the rate limit.
- **Public data only.** Emails are used only for the login mapping ([domain](domain.md#open-questions)), then
  dropped.

## Sources

| Source | Fetch | Watermark | Can change after first seen? |
|---|---|---|---|
| Commits, file changes | `git fetch`, then `git log <last-sha>..origin/<default-branch> --numstat` | last synced sha | no |
| Pull requests | `GET /repos/{o}/{r}/pulls?state=all&sort=updated&direction=desc`, stopping at the watermark | max `updated_at` | yes: state, labels, merge |
| Releases | `GET /repos/{o}/{r}/releases`, paged until a known tag | latest `published_at` | rarely |
| Workflow runs | `GET /repos/{o}/{r}/actions/runs?created=>=<watermark>`, plus a re-fetch of runs not yet completed | max `created_at` and the incomplete run ids | until completed; re-runs add an attempt |
| Jobs | `GET /repos/{o}/{r}/actions/runs/{id}/attempts/{n}/jobs` for new or changed attempts only | per run attempt | no, once the attempt completes |
| Contributors | derived from commit and PR authors; `GET /users/{login}` once per new login | — | rarely |

**History rewrites:** before using the last synced sha, sync checks it is still an ancestor of the default branch
(`git merge-base --is-ancestor`). If it isn't, sync rescans commits from scratch.

## Seed snapshot

- `seed/` is committed, so a fresh clone runs with no token and no network.
- It holds one file per table plus a manifest of the watermarks at export time.
- On first boot, an empty database loads the seed and takes its watermarks. The first sync after a clone is then a
  delta from the seed, not a full scan.
- `sync` refreshes the seed as well as the database.

## Inputs

- The repository to read, `airbnb/viaduct` by default.
- A GitHub token from `GITHUB_TOKEN`, used only by sync, never by `bootRun`.
- A local clone directory, `.pulse/repo`, which is gitignored.

## Open questions

S01 decides these unless noted.

- **Process model.** Embedded Postgres has one owner per data directory. Options:
  - sync runs inside the app, from a "Sync now" button or a startup flag;
  - a separate CLI starts its own embedded Postgres while the app is stopped.
- **Seed format.** Candidates: Postgres `COPY` CSV, gzipped; or SQL inserts; or NDJSON. The choice depends on file
  size and readable diffs.
- **CI history size (S08).** The number of workflow runs and jobs in `airbnb/viaduct` hasn't been measured. Measure
  it, then decide whether to keep only a time window.
- **Clone type.** `--numstat` needs file contents, so a blobless clone would fetch blobs on demand during `git log`.
  The full clone size hasn't been measured.
