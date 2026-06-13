# Rendering Design

## Scope
This document defines the printable invoice document and renderer-neutral layout boundaries. It is not an HTML, PDF, JavaFX preview, or printer implementation.

## Document Boundary
The document slice starts at `DocumentPort` in `com.plaininvoice.invoice.document.printable`.

`DocumentPort` accepts a `DocumentRequest` containing an `Invoice` aggregate and deterministic document metadata. It returns an immutable `InvoiceDocument` value that downstream renderers can translate into layout tokens, HTML, PDF, preview panes, or page output.

The document slice must not depend on JavaFX, persistence, PDF libraries, printer APIs, or HTML builders. Those are edge adapters that consume document/layout values later.

## Document Shape
`InvoiceDocument` is composed from small immutable records:
- `DocumentMeta`: title and language
- `DocumentHeader`: invoice number, issue date, and state
- `DocumentParties`: seller and buyer document parties
- `DocumentLine`: ordered line content, quantity, tax rate, and line amounts
- `DocumentTotals`: subtotal, tax, and total due
- `DocumentTerms`: due date and payment note

`BuildDocument` maps the invoice aggregate into this shape without changing invoice behavior.

## Layout Boundary
The layout slice starts at `LayoutPort` in `com.plaininvoice.invoice.document.layout`.

`LayoutPort` accepts an `InvoiceDocument` and returns immutable `LayoutDocument` tokens. These tokens express layout intent, not renderer instructions.

Layout tokens include:
- `LayoutPage`: page size, orientation, and margins
- `HeaderToken`: title, invoice number, issue date, and state
- `PartyBlock`: seller and buyer party blocks
- `LineTableToken`: ordered invoice line tokens
- `TotalsToken`: subtotal, tax, and total due block
- `TermsToken`: payment due date and note
- `FooterToken`: deterministic footer text
- `PageHints`: running header/footer and table break hints

## Determinism
Document and layout construction must be deterministic:
- preserve invoice line order
- assign line positions from `1`
- derive amounts from domain methods (`subtotal`, `tax`, `total`)
- keep caller-provided metadata explicit
- defensively copy document and layout line collections
- use explicit page and break hints instead of renderer defaults

This keeps future golden-file tests viable for HTML, PDF, CSV, preview, and page output.

## Downstream Work Blocked By Layout Tokens
The following items should build on `LayoutDocument` instead of reading the domain aggregate directly:
- pagination rules and page frames
- export acceptance fixtures and golden-file comparisons
- HTML export
- PDF export
- JavaFX invoice preview
- page output flow
