package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceTaxKey(String id, String lineId, String label) {
  public InvoiceTaxKey {
    Objects.requireNonNull(id, "invoice tax id cannot be null");
    Objects.requireNonNull(lineId, "invoice tax line id cannot be null");
    Objects.requireNonNull(label, "invoice tax label cannot be null");
    id = id.trim();
    lineId = lineId.trim();
    label = label.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice tax id cannot be blank");
    }
    if (lineId.isEmpty()) {
      throw new IllegalArgumentException("invoice tax line id cannot be blank");
    }
    if (label.isEmpty()) {
      throw new IllegalArgumentException("invoice tax label cannot be blank");
    }
  }
}
