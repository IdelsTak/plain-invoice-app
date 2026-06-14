# Contributing to plain-invoice-app

Thanks for contributing.

## Project-first workflow (required)
All work must start in GitHub Project `#27`:
1. Create/refine Project item
2. Ensure fields are set (`Priority`, `Area`, `Type`, `Milestone Bucket`, `Target Date` for `P0`)
3. Convert the existing linked draft item to a repository issue (do not create duplicate issues)
4. Move converted issue item from `Todo` to `Ready` (keep `Todo` mainly for draft items)
5. If the issue depends on unfinished predecessor work, set issue dependency metadata (`blocked by`) and move status to `Blocked`
6. Assign repository milestone (`M1`..`M4`)
7. Move Project item to `In Progress` only when actively implementing
8. Implement and open PR linked to issue

Dependency status handling:
- `Ready`: issue is unblocked and can be picked now.
- `Blocked`: issue cannot start until its `blocked by` issues are `Done`; once unblocked, move it back to `Ready`.

## Development setup
- Java 26+
- Maven 3.9.14

## Commands
- Fast local tests: `mvn -B -ntp test`
- Fast local verify: `mvn -B -ntp verify`
- Authoritative CI gate: `mvn -B -ntp clean verify`
- Run app: `mvn -B -ntp javafx:run`

## Testing policy
- Unit tests use JUnit + Hamcrest (`*Test.java`)
- UI integration tests use TestFX (`*IT.java`)
- Behavior changes must include tests
- JaCoCo coverage is enforced in the authoritative gate `mvn -B -ntp clean verify`
- GitHub Actions may cache Maven dependencies via `actions/setup-java`, but CI must not reuse generated build output such as `target/`

## Code consistency rules (mandatory)
- Use `sealed` hierarchies for closed domain variants instead of enums where variant behavior/state transitions matter.
- When all variants are nested in the sealed type, do not add an explicit `permits` list.
- Prefer pattern-oriented state handling over repeated `instanceof` checks.
- Use unnamed pattern `_` for intentionally unused pattern bindings.
- Avoid `DEFAULT` singleton instances for behavior policies; use default constructors.
- Use `var` where inferred type is obvious and readability is preserved.
- Defensively copy collection inputs for stored fields/components using `List.copyOf`, `Set.copyOf`, `Map.copyOf`, or an owned mutable copy when internal mutation is required; explicit collection accessors must return immutable snapshots instead of backing fields.
- Keep transition logic centralized (shared transition helpers) instead of duplicating per-action branching.
- Keep application contracts immutable and explicit using records.
- Keep contract records small via composition of related value objects.
- Avoid nested contract type sprawl; prefer top-level contract types.
- Keep one top-level type per `.java` file.
- Organize packages as capability-first vertical slices (`<bounded-context>.<capability>`) instead of technical-layer buckets.
- Keep root capability packages small; move persistence implementation details into named storage capability slices such as `local`, `backup`, `audit`, and `sqlite`.
- Keep rendering document concerns split into focused slices: `document.printable` for printable invoice contracts, `document.layout` for renderer-neutral layout tokens, and `document.pagination` for page-frame pagination; the `document` root must not contain production types.
- Keep export adapter tests anchored to `docs/EXPORT-ACCEPTANCE.md`; compare canonical fixtures with format-specific golden rules instead of weakening assertions for adapter convenience.
- Keep export adapters in `invoice.exporting`; they consume page/document values and must not leak HTML, PDF, CSV, or printer concerns back into domain/rendering contracts.
- Keep application use-cases as thin orchestrators; keep business rules in domain objects.
- Keep class names concise (max 30 characters) and avoid technical-role suffixes (`Parser`, `Manager`, `Service`, `Model`, `Dao`, `Helper`, `Processor`, `Factory`).
- Keep method names concise (max 18 characters).
- Test classes must mirror production class names (`Foo` -> `FooTest`).
- Keep one assert per test method.

## Pull request rules
- PR title must be semantic: `<type>: <summary>`
- Allowed title types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `ci`, `build`, `perf`
- PR body must contain only `## What changed` with concise commit-change summary bullets
- Include a closing keyword bullet for the implemented issue inside `## What changed` (for example `Closes #36`) so GitHub links the PR under the issue Development section and closes the issue on merge
- Do not include extra PR-body sections beyond `## What changed`
- PR should contain one logical change set
- `main` is updated via squash merge; PR title becomes final commit title

## Mandatory pre-coding issue gate
Before starting implementation for any issue:
1. Set assignee
2. Link issue to Project `#27`
3. Ensure Project fields are complete: `Status`, `Priority`, `Area`, `Type`, `Milestone Bucket`, `Effort`, `Target Date` (for `P0`)
4. Ensure repository milestone is assigned and matches Project milestone bucket

Do not start coding until this metadata gate is complete.

Milestone progress note:
- If milestone completion appears stale (for example 100% with open issues), re-save milestone assignments on the affected issues (`remove` then `set` the same milestone) to force GitHub to recalculate counters.

## Effort scale
Use Fibonacci-style effort estimates in Project #27:
- `1` trivial
- `2` very small
- `3` small
- `5` medium
- `8` large
- `13` very large (split recommended)

## Issue body format (mandatory)
Issue bodies must contain:
- concise prose description of problem and scope
- `## Acceptance Criteria` section with checkboxes

Do not use dedicated `## Problem` or `## Scope` headings.
Use repository issue templates or match the same structure exactly.

## Feature PR metadata gate (mandatory)
Before requesting review on a feature PR:
1. Set assignee
2. Add labels (`type:*`, `area:*`)
3. Set milestone matching Project milestone bucket
4. Link PR to Project `plain-invoice Project` (`#27`)
5. Set Project PR-item fields: `Status`, `Priority`, `Area`, `Type`, `Milestone Bucket`, `Effort`, and `Target Date` for `P0`
6. Link the PR to its issue with a closing keyword in the PR body so the issue Development section tracks the PR

PRs are not review-ready until this gate is complete.
