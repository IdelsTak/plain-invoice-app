package com.plaininvoice.invoice.document.pagination;

import com.plaininvoice.invoice.document.layout.*;
import java.util.*;

public record PageBody(
  PartyBlock parties,
  LineTableToken lines,
  TotalsToken totals,
  TermsToken terms,
  boolean finalPage
) {
  public PageBody {
    Objects.requireNonNull(parties, "page parties cannot be null");
    Objects.requireNonNull(lines, "page lines cannot be null");
    Objects.requireNonNull(totals, "page totals cannot be null");
    Objects.requireNonNull(terms, "page terms cannot be null");
  }
}
