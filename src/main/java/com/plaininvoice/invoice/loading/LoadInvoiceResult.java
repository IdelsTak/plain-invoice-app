package com.plaininvoice.invoice.loading;

import java.util.*;

public record LoadInvoiceResult(Optional<InvoiceDetail> invoice) {
  public LoadInvoiceResult {
    invoice = Objects.requireNonNull(invoice, "invoice detail cannot be null");
  }
}
