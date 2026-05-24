package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.lifecycle.*;
import com.plaininvoice.invoice.draft.*;
import java.util.*;

public record EditInvoiceRequest(Invoice current, InvoiceDraftSpec spec) {
  public EditInvoiceRequest {
    Objects.requireNonNull(current, "current invoice cannot be null");
    Objects.requireNonNull(spec, "invoice draft spec cannot be null");
  }
}
