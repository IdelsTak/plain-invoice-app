package com.plaininvoice.invoice.document;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record DocumentRequest(Invoice invoice, DocumentMeta meta) {
  public DocumentRequest {
    Objects.requireNonNull(invoice, "invoice cannot be null");
    Objects.requireNonNull(meta, "document metadata cannot be null");
  }
}
