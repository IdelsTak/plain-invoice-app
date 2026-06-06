package com.plaininvoice.invoice.storage;

import java.util.*;

public record InvoiceStoreKey(String id, long version) {
  public InvoiceStoreKey {
    Objects.requireNonNull(id, "invoice storage id cannot be null");
    id = id.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice storage id cannot be blank");
    }
    if (version < 0) {
      throw new IllegalArgumentException("invoice version cannot be negative");
    }
  }
}
