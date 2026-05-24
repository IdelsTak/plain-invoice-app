# Contributing to plain-invoice-app

Thanks for contributing.

## Project-first workflow (required)
All work must start in GitHub Project `#27`:
1. Create/refine Project item
2. Ensure fields are set (`Priority`, `Area`, `Type`, `Milestone Bucket`, `Target Date` for `P0`)
3. Convert/create linked repository issue
4. Assign repository milestone (`M1`..`M4`)
5. Move Project item to `In Progress`
6. Implement and open PR linked to issue

## Development setup
- Java 26+
- Maven 3.9.14

## Commands
- Unit tests: `mvn -B -ntp test`
- Full verify: `mvn -B -ntp verify`
- Run app: `mvn -B -ntp javafx:run`

## Testing policy
- Unit tests use JUnit + Hamcrest (`*Test.java`)
- UI integration tests use TestFX (`*IT.java`)
- Behavior changes must include tests

## Pull request rules
- PR title must be semantic: `<type>: <summary>`
- Allowed title types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `ci`, `build`, `perf`
- PR body must contain only `## What changed` with concise commit-change summary bullets
- Do not include extra PR-body sections beyond `## What changed`
- PR should contain one logical change set

## Mandatory pre-coding issue gate
Before starting implementation for any issue:
1. Set assignee
2. Link issue to Project `#27`
3. Ensure Project fields are complete: `Status`, `Priority`, `Area`, `Type`, `Milestone Bucket`, `Effort`, `Target Date` (for `P0`)
4. Ensure repository milestone is assigned and matches Project milestone bucket

Do not start coding until this metadata gate is complete.

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

PRs are not review-ready until this gate is complete.
