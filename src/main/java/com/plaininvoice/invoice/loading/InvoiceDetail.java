package com.plaininvoice.invoice.loading;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.storage.*;
import java.util.*;

public record InvoiceDetail(String id, long version, Invoice invoice) {
  public InvoiceDetail {
    Objects.requireNonNull(id, "invoice id cannot be null");
    Objects.requireNonNull(invoice, "invoice cannot be null");
    id = id.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice id cannot be blank");
    }
  }

  static InvoiceDetail from(StoredInvoice stored) {
    Objects.requireNonNull(stored, "stored invoice cannot be null");
    return new InvoiceDetail(stored.meta().key().id(), stored.meta().key().version(), stored.invoice());
  }
}
