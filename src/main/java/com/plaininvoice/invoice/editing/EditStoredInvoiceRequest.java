package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.draft.*;
import com.plaininvoice.invoice.storage.*;
import java.time.*;
import java.util.*;

public record EditStoredInvoiceRequest(StoredInvoice current, InvoiceDraftSpec spec, Instant updatedAt) {
  public EditStoredInvoiceRequest {
    Objects.requireNonNull(current, "current stored invoice cannot be null");
    Objects.requireNonNull(spec, "invoice draft spec cannot be null");
    Objects.requireNonNull(updatedAt, "updated timestamp cannot be null");
  }
}
