package com.plaininvoice.invoice.storage;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record StoredInvoice(InvoiceStoreMeta meta, Invoice invoice) {
  public StoredInvoice {
    Objects.requireNonNull(meta, "stored invoice metadata cannot be null");
    Objects.requireNonNull(invoice, "stored invoice cannot be null");
  }
}
