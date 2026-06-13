# Rendering Design

## Scope
This document defines the printable invoice document boundary. It is not an HTML, PDF, JavaFX preview, or printer implementation.

## Boundary
The rendering slice starts at `DocumentPort` in `com.plaininvoice.invoice.document`.

`DocumentPort` accepts a `DocumentRequest` containing an `Invoice` aggregate and deterministic document metadata. It returns an immutable `InvoiceDocument` value that downstream renderers can translate into HTML, PDF, preview panes, or print jobs.

The document slice must not depend on JavaFX, persistence, PDF libraries, printer APIs, or HTML builders. Those are edge adapters that consume `InvoiceDocument` later.

## Document shape
`InvoiceDocument` is composed from small immutable records:
- `DocumentMeta`: title and language
- `DocumentHeader`: invoice number, issue date, and state
- `DocumentParties`: seller and buyer document parties
- `DocumentLine`: ordered line content, quantity, tax rate, and line amounts
- `DocumentTotals`: subtotal, tax, and total due
- `DocumentTerms`: due date and payment note

`BuildDocument` maps the invoice aggregate into this shape without changing invoice behavior.

## Determinism
Document construction must be deterministic:
- preserve invoice line order
- assign line positions from `1`
- derive amounts from domain methods (`subtotal`, `tax`, `total`)
- keep caller-provided metadata explicit
- defensively copy document line collections

This keeps future golden-file tests viable for HTML, PDF, CSV, and print-preview output.

## Downstream work blocked by this contract
The following items should build on `InvoiceDocument` instead of reading the domain aggregate directly:
- layout tokens and document style rules
- pagination rules
- export acceptance fixtures and golden-file comparisons
- HTML export
- PDF export
- JavaFX invoice preview
- print flow
