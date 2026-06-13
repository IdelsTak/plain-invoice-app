package com.plaininvoice.invoice.document.printable;

import java.util.*;

public record InvoiceDocument(
  DocumentMeta meta,
  DocumentHeader header,
  DocumentParties parties,
  List<DocumentLine> lines,
  DocumentTotals totals,
  DocumentTerms terms
) {
  public InvoiceDocument {
    Objects.requireNonNull(meta, "document metadata cannot be null");
    Objects.requireNonNull(header, "document header cannot be null");
    Objects.requireNonNull(parties, "document parties cannot be null");
    Objects.requireNonNull(lines, "document lines cannot be null");
    Objects.requireNonNull(totals, "document totals cannot be null");
    Objects.requireNonNull(terms, "document terms cannot be null");
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("document must contain at least one line");
    }
    lines = List.copyOf(lines);
  }
}
