package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceLineKey(String id, String invoiceId, int position) {
  public InvoiceLineKey {
    Objects.requireNonNull(id, "invoice line id cannot be null");
    Objects.requireNonNull(invoiceId, "invoice line parent id cannot be null");
    id = id.trim();
    invoiceId = invoiceId.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice line id cannot be blank");
    }
    if (invoiceId.isEmpty()) {
      throw new IllegalArgumentException("invoice line parent id cannot be blank");
    }
    if (position < 1) {
      throw new IllegalArgumentException("invoice line position must be positive");
    }
  }
}
