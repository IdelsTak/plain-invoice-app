package com.plaininvoice.invoice.loading;

import java.util.*;

public record LoadInvoiceRequest(String id) {
  public LoadInvoiceRequest {
    Objects.requireNonNull(id, "invoice id cannot be null");
    id = id.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice id cannot be blank");
    }
  }
}
