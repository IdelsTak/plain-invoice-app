package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.storage.*;
import java.util.*;

public sealed interface EditStoredInvoiceResult {
  record Saved(StoredInvoice invoice) implements EditStoredInvoiceResult {
    public Saved {
      Objects.requireNonNull(invoice, "stored invoice cannot be null");
    }
  }

  record Conflict(String id, String reason) implements EditStoredInvoiceResult {
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
