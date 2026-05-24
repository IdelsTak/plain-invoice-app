# ADR-0002: Persistence Boundaries and Optimistic Concurrency

## Status
Accepted

## Context
Invoice persistence must preserve aggregate consistency, support deterministic updates, and avoid silent overwrites when concurrent modifications occur.

## Decision
- Persist invoice aggregate using relational tables with explicit parent-child foreign keys.
- Execute aggregate create/update inside single database transactions opened with `BEGIN IMMEDIATE`.
- Use optimistic concurrency via `version` on `invoices`.
- Require update statements to match both `id` and expected `version`.
- Treat zero-row updates as concurrency conflicts.
- Store money as integer minor units in persistence to avoid floating-point precision drift.
- Make invoice header currency authoritative and keep child currency values consistent with it.
- Support multiple tax components per line using `invoice_taxes(invoice_line_id, tax_label)` uniqueness.

## Consequences
Positive:
- Prevents lost updates.
- Keeps aggregate writes atomic.
- Maintains deterministic behavior at repository boundary.

Trade-offs:
- Callers must handle concurrency conflict outcomes.
- Update flow must load and pass expected `version`.

## Follow-up
- Implement schema migration (`#24`).
- Implement SQLite adapter with conflict signaling (`#20`).
