# plain-invoice-app

`plain-invoice-app` exists for people who want invoicing software that feels dependable, understandable, and local-first.

Most invoice tools are either too bloated, too web-centric, or too opaque about how totals and taxes are calculated. This project takes a different route: a desktop app where invoice behavior is explicit in code, calculations are deterministic, and your data stays under your control.

## Why this repo exists
- Small teams and solo operators still need serious invoicing
- Financial calculations should be precise, auditable, and predictable
- Desktop workflows are still valuable when you want speed and ownership over your data
- Long-lived software should prioritize clarity over framework hype

## Inspiration
This project is inspired by domain-driven design and by the idea that business software should model real business language directly:
- invoices
- line items
- money
- taxes
- payment terms

Instead of treating invoices like loose form fields, the app treats them as real domain objects with explicit rules.

## What problem it solves
`plain-invoice-app` is being built to help you:
- Create and edit invoices confidently
- Keep totals and taxes consistent every time
- Store invoices locally
- Export documents (PDF/CSV/HTML)
- Print invoices from a desktop workflow

## Project philosophy
- Domain-first over UI-first
- Explicit rules over hidden magic
- Immutable value objects over mutable state drift
- Fail fast over silent corruption
- Simple monolith over premature distributed complexity

## Current status
Early build phase.
The repository currently contains a monolithic Maven + JavaFX scaffold, initial domain primitives, and a prioritized GitHub Project backlog.

## Quick start
Requirements:
- Java 26+
- Maven 3.9.14

Run tests:
```bash
mvn -B -ntp test
```

Run the desktop app:
```bash
mvn -B -ntp javafx:run
```

## Roadmap focus
The next delivery focus is:
1. Domain core (money, lifecycle, numbering, invariants)
2. Persistence (local storage, transactions, backup/restore)
3. Rendering and export
4. Desktop UI and print pipeline

## Contributing
Contributions are welcome, especially around domain modeling, deterministic calculations, and desktop UX quality.

For working conventions and agent execution rules, see local project guidance in the parent workspace documentation.

## Community health
- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [Security Policy](SECURITY.md)
- [Support](SUPPORT.md)
- [Changelog](CHANGELOG.md)
