package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record LayoutDocument(
  DocumentMeta meta,
  LayoutPage page,
  HeaderToken header,
  PartyBlock parties,
  LineTableToken lines,
  TotalsToken totals,
  TermsToken terms,
  FooterToken footer,
  PageHints hints
) {
  public LayoutDocument {
    Objects.requireNonNull(meta, "layout metadata cannot be null");
    Objects.requireNonNull(page, "layout page cannot be null");
    Objects.requireNonNull(header, "layout header cannot be null");
    Objects.requireNonNull(parties, "layout parties cannot be null");
    Objects.requireNonNull(lines, "layout lines cannot be null");
    Objects.requireNonNull(totals, "layout totals cannot be null");
    Objects.requireNonNull(terms, "layout terms cannot be null");
    Objects.requireNonNull(footer, "layout footer cannot be null");
    Objects.requireNonNull(hints, "layout hints cannot be null");
  }
}
