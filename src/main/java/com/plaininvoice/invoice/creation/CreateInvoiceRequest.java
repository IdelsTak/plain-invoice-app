package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.draft.*;
import java.util.*;

public record CreateInvoiceRequest(InvoiceDraftSpec spec) {
  public CreateInvoiceRequest {
    Objects.requireNonNull(spec, "invoice draft spec cannot be null");
  }
}
