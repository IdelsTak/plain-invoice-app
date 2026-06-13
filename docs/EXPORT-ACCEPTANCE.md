# Export Acceptance

## Scope
This document defines how future HTML, PDF, and CSV export adapters are accepted. It does not define exporter implementations.

Export acceptance tests should prove that an adapter preserves invoice meaning across deterministic fixture cases without binding tests to incidental implementation details.

## Canonical Cases
Use the canonical cases in `src/test/java/com/plaininvoice/invoice/exporting/ExportCases.java`:
- `simple`: one untaxed line for base document shape
- `multi-line`: ordered line table content
- `taxed`: tax-bearing totals
- `rounding`: fractional quantity and monetary rounding
- `long-content`: wrapping and page-frame pressure

New export adapters must cover each canonical case unless the adapter scope explicitly excludes that case.

## Comparison Rules
HTML export:
- compare semantic markup and visible text
- normalize line endings to `LF`
- keep escaping, element order, and key attributes deterministic
- do not normalize away invoice values, ordering, tax values, totals, or payment terms
- build from `PageDocument` so HTML preview/export uses the same page-frame sequence as PDF and print

PDF export:
- compare extracted text and explicit document metadata
- normalize line endings to `LF`
- keep fonts, page ordering, page numbers, and metadata deterministic
- use binary comparison only after font embedding and metadata are stable enough to avoid machine-specific noise

CSV export:
- use UTF-8
- use `CRLF` record separators
- compare headers, column order, quoting, escaping, and records according to RFC 4180
- do not normalize commas, quotes, embedded line breaks, empty fields, currency values, tax values, or totals

## Volatile Output
Only normalize values that are inherently volatile and not part of invoice meaning:
- platform line endings for text comparisons
- generated timestamps when the adapter accepts an explicit test clock
- PDF producer metadata when the chosen PDF library cannot make it deterministic

Do not normalize invoice numbers, issue dates, due dates, party data, line order, quantities, money, taxes, totals, page numbers, or CSV column order.

## Fixture Updates
Fixture updates must be reviewed as behavior changes:
- explain why the fixture changed
- update expected files in the same pull request as the adapter behavior
- keep one canonical case per business concern
- prefer adding a new case over weakening comparison rules

## HTML Adapter
The HTML adapter starts at `HtmlPort` in `com.plaininvoice.invoice.exporting`.

`HtmlPort` accepts a `PageDocument` and returns a deterministic UTF-8 `HtmlPage`. It is an export edge: it may know about HTML, but domain, printable document, layout, and pagination slices must not know about it.

HTML output must:
- escape text explicitly
- include invoice header, parties, lines, totals, terms, footer, and page numbers
- keep CSS deterministic and self-contained enough for local preview
- avoid volatile timestamps or environment-specific values

## Research References
- RFC 4180 defines common CSV record, header, quoting, and line-break conventions: https://www.rfc-editor.org/info/rfc4180/
- Apache PDFBox provides Java APIs for PDF creation and text extraction that can be used by PDF adapter tests: https://pdfbox.apache.org/
- Java `Charset` defines UTF-8 as a standard charset available on every Java implementation: https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/nio/charset/StandardCharsets.html
