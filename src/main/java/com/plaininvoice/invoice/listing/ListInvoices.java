package com.plaininvoice.invoice.listing;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public final class ListInvoices {
  private final InvoiceRepository invoices;

  public ListInvoices(InvoiceRepository invoices) {
    this.invoices = Objects.requireNonNull(invoices, "invoice repository cannot be null");
  }

  public ListInvoicesResult execute() {
    var summaries = invoices.list().stream()
      .map(InvoiceSummary::from)
      .toList();
    return new ListInvoicesResult(summaries);
  }
}
