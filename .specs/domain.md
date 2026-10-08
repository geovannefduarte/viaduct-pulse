# Domain: Pulse

Pulse answers "what is happening in the Viaduct repository?": who contributes, what changes, how CI behaves. It
reads one configured repository, `airbnb/viaduct` by default.

## Entities

| Entity | Source | Key | Holds |
|---|---|---|---|
| Repository | GitHub API | `owner/name` | description, default branch |
| Contributor | git authors and GitHub API | GitHub login | login, display name, avatar URL; never email |
| Commit | local git clone | sha | message, authored and committed times, author, parents |
| FileChange | `git log --numstat` | (sha, path) | additions, deletions |
| PullRequest | GitHub API | number | state, author, created/merged/closed times, labels, merge commit |
| Release | GitHub API | tag | published time, target commit |
| WorkflowRun | GitHub API | (run id, attempt) | workflow, event, branch, head sha, conclusion, start and end |
| Job | GitHub API | job id | run, name, runner OS, conclusion, duration |
| Annotation | Pulse mutations | id | a maintainer's note on a run or job, for example "flaky" |

Resolvers compute these instead of storing them:
- lines added and removed per contributor and per release;
- CI duration percentiles and failure rate per workflow and job;
- flakiness, meaning a job that failed and then passed on a re-run of the same commit.

## Tenants

The target split, from S08:

| Tenant | Owns | Reads from other tenants |
|---|---|---|
| `code` | Repository, Commit, FileChange, PullRequest, Release | — |
| `people` | Contributor | `code` for commits; `ci` for a contributor's CI failure rate |
| `ci` | WorkflowRun, Job, Annotation | `code` for a run's commit |

S02 through S07 use one tenant module. S08 splits it, and the split is that chapter's multi-tenancy lesson.

## Viewers

There are two simulated viewers, chosen in the UI: **anonymous** and **maintainer**.
- The maintainer also sees CI internals and annotations, and can annotate.
- S10 carries the viewer as Viaduct request context.
- S11 turns it into a public schema and a maintainer schema.

## Schema sketch

This is only an illustration. Each slice owns its real schema. Fields are nullable unless the type system
guarantees a value.

```graphql
type Query {
  repository: Repository
}

type Repository {
  name: String
  commits(first: Int, after: String): CommitConnection
  contributors(first: Int, after: String): ContributorConnection
  pullRequests(first: Int, after: String): PullRequestConnection
  workflowRuns(first: Int, after: String): WorkflowRunConnection
  releases: [Release]
}

type Commit implements Node {
  id: ID!
  sha: String
  message: String
  author: Contributor
  changes: [FileChange]
  pullRequest: PullRequest
  workflowRuns: [WorkflowRun]
}

type Contributor implements Node {
  id: ID!
  login: String
  name: String
  avatarUrl: String
  commits(first: Int, after: String): CommitConnection
  ciFailureRate: Float
}
```

## Open questions

- **Mapping git authors to logins.** A commit read from git has an email, not a login. Options:
  - take the login from the GitHub API's commit list;
  - parse GitHub noreply addresses (`<id>+<login>@users.noreply.github.com`) before dropping the email.

  S01 decides.
