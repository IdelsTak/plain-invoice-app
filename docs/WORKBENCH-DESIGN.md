# Workbench Design

## Scope
This document defines how JavaFX screens, view models, and application use cases communicate. It keeps JavaFX presentation thin, keeps UI state out of domain objects, and defines how background work reports results back to the UI.

## Layer boundary
The desktop flow has four roles:

1. `Screen`
   - Owns JavaFX nodes, bindings, cell factories, and navigation wiring.
   - Reads and writes only JavaFX-facing state.
   - Never mutates domain objects and never talks to repositories, exporters, or printers directly.

2. `ViewModel`
   - Owns observable UI state for one screen.
   - Maps raw control state into explicit command/query requests.
   - Calls application use cases through small runner methods.
   - Maps use-case results into screen-facing state and events.

3. `Application use case`
   - Accepts immutable command/query records.
   - Runs domain rules and delegates side effects to repository, export, or print ports.
   - Returns immutable result values or raises explicit validation faults.

4. `Edge adapter`
   - SQLite, backup/restore, HTML/PDF/CSV export, printer integration, and filesystem work.
   - Runs outside the JavaFX Application Thread.

## Command/query flow
View models must call application code through explicit commands and queries rather than passing live JavaFX state objects.

Flow:

1. Screen event fires on the JavaFX Application Thread.
2. View model reads current UI fields into a screen state snapshot.
3. View model builds an application request record such as:
   - `CreateStoredInvoiceRequest`
   - `EditStoredInvoiceRequest`
   - `LoadInvoiceRequest`
4. View model submits that request to a use-case runner.
5. Runner executes the use case and returns one immutable result.
6. View model maps the result into:
   - observable field values
   - validation messages
   - conflict banners/dialog state
   - navigation events
   - progress/busy state

Rules:
- JavaFX properties stay in the screen/view-model layer.
- Domain aggregates such as `Invoice` are never partially mutated by the UI.
- Screens may display domain-derived values, but domain behavior stays in domain/use-case code.
- A screen must not hold a repository reference or construct persistence/export/print adapters.
- A view model must not start overlapping workers for the same user intent by default. While a command is running, the default policy is:
  - disable or ignore duplicate submit actions for that same operation
  - allow explicit cancel only when the worker supports cancellation safely
  - allow a later screen-specific override only when the flow defines a clear replacement policy
- Request queuing is not the default. If a flow needs queued or replace-latest behavior, that policy must be explicit in the view model instead of implicit in JavaFX event ordering.

## Validation and conflict reporting
Expected application outcomes must be reported back to the UI explicitly.

### Validation
- Domain and application validation failures are reported with `ValidationFault`.
- `ValidationFault` is the aggregate failure contract; it wraps one or more `ValidationError` values for UI mapping.
- View models catch `ValidationFault` and map each `ValidationError` into screen-facing error state using:
  - `path` for field targeting
  - `code` for stable UI message lookup
  - `context` for interpolation details
- Screens render those mapped errors next to fields or in form-level summaries.
- Screens do not inspect domain exceptions other than the explicit validation contract.

### Repository conflicts
- Optimistic concurrency conflicts are expected outcomes, not generic UI failures.
- Create/edit store use cases already model this through explicit `Conflict` results.
- View models map a `Conflict` result into:
  - non-field conflict message state
  - optional reload/refresh affordance
  - no silent overwrite of current form state
- Screens present conflict state, but they do not decide repository retry rules.

### Unexpected failures
- Unexpected infrastructure errors remain exceptional.
- View models map them to generic operation-failed state and keep prior screen state intact where possible.
- Domain and repository ports must not leak JavaFX types in those failures.

## Threading rules
JavaFX screens must stay responsive. Any work that touches storage, backup/restore, export, or print runs off the JavaFX Application Thread.

### JavaFX Application Thread
Allowed work:
- control event handling
- small state mapping
- binding updates
- navigation changes
- applying completed results to observable state

Not allowed:
- SQLite open/load/save/list/backup/restore calls
- PDF/HTML/CSV export that writes files or renders documents
- printer discovery or print submission
- filesystem access that can block the UI

### Background work
- Persistence, export, and print operations run in `Task` or `Service` instances backed by a dedicated executor.
- `Worker` state (`READY`, `RUNNING`, `SUCCEEDED`, `FAILED`, `CANCELLED`) is the UI-visible lifecycle for long-running work.
- View models expose busy/progress/cancel state derived from the `Worker`, not from ad hoc booleans spread across screens.
- Domain-only computations may run inline only when they are small and deterministic. In practice that means:
  - no filesystem, database, network, export, or printer I/O
  - no waits, sleeps, locks, or background coordination
  - no iteration over unbounded or user-history-sized datasets
- Once a flow touches repository, export, backup, restore, or printer edges, the whole operation belongs on a background worker.

### Result handoff
- Worker success/failure handlers update JavaFX observable state on the JavaFX Application Thread.
- Screens observe view-model state changes; they do not poll use cases or background threads directly.
- Cancellation is a UI concern exposed by the worker boundary; domain aggregates do not model JavaFX task state.

## Screen listening model
Screens listen to view-model state, not domain behavior.

Recommended screen inputs from a view model:
- field values
- field error map
- form-level message
- busy flag
- progress text/value
- one-shot navigation or dialog events
- loaded list/detail projections for rendering

Recommended screen outputs to a view model:
- user intent events such as save, refresh, export, print, cancel, and navigate

Rules:
- Screens bind controls to view-model properties or immutable screen-state snapshots.
- View models may expose small UI projections for tables and forms; those projections are read models, not domain objects with behavior.
- A screen must never call domain methods to decide business outcomes. It only renders the result already decided by the use case.

## Rendering and export fit
- JavaFX preview builds from `PageDocument`, the immutable ordered page-frame result produced by the rendering pipeline, as described in `docs/RENDERING-DESIGN.md`.
- HTML and PDF export remain edge adapters over document/page values.
- The workbench layer orchestrates those flows but does not move rendering rules into JavaFX controls.

## Persistence fit
- Settings defaults and persisted invoice operations remain application contracts as described in `docs/PERSISTENCE-DESIGN.md`.
- The workbench layer may choose when to load or refresh data, but repository conflict detection and audit behavior stay below the UI boundary.
