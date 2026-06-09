package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public record CreateStoredInvoiceRequest(String id, StoreClock clock, CreateInvoiceRequest invoice) {
  public CreateStoredInvoiceRequest {
    Objects.requireNonNull(id, "invoice id cannot be null");
    Objects.requireNonNull(clock, "invoice storage clock cannot be null");
    Objects.requireNonNull(invoice, "create invoice request cannot be null");
    id = id.trim();
    if (id.isEmpty()) {
      throw new IllegalArgumentException("invoice id cannot be blank");
    }
  }
}
