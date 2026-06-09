package com.plaininvoice.invoice.loading;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public final class LoadInvoice {
  private final InvoiceRepository invoices;

  public LoadInvoice(InvoiceRepository invoices) {
    this.invoices = Objects.requireNonNull(invoices, "invoice repository cannot be null");
  }

  public LoadInvoiceResult execute(LoadInvoiceRequest request) {
    Objects.requireNonNull(request, "load invoice request cannot be null");
    var detail = invoices.load(request.id()).map(InvoiceDetail::from);
    return new LoadInvoiceResult(detail);
  }
}
