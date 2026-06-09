package com.plaininvoice.invoice.listing;

import java.util.*;

public record ListInvoicesResult(List<InvoiceSummary> invoices) {
  public ListInvoicesResult {
    Objects.requireNonNull(invoices, "invoice summaries cannot be null");
    invoices = List.copyOf(invoices);
  }
}
