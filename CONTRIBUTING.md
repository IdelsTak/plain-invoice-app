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
- Java 21+
- Maven 3.9+

## Commands
- Unit tests: `mvn -B -ntp test`
- Full verify: `mvn -B -ntp verify`
- Run app: `mvn -B -ntp javafx:run`

## Testing policy
- Unit tests use JUnit + Hamcrest (`*Test.java`)
- UI integration tests use TestFX (`*IT.java`)
- Behavior changes must include tests

## Pull request rules
- PR must link issue with closing keyword, e.g. `Closes #123`
- PR must include test evidence
- PR should contain one logical change set
