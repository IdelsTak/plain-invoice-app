package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public sealed interface CreateStoredInvoiceResult {
  record Saved(StoredInvoice invoice) implements CreateStoredInvoiceResult {
    public Saved {
      Objects.requireNonNull(invoice, "stored invoice cannot be null");
    }
  }

  record Conflict(String id, String reason) implements CreateStoredInvoiceResult {
    public Conflict {
      Objects.requireNonNull(id, "invoice id cannot be null");
      reason = reason == null ? "" : reason.trim();
      id = id.trim();
      if (id.isEmpty()) {
        throw new IllegalArgumentException("invoice id cannot be blank");
      }
    }
  }
}
