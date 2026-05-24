package com.plaininvoice.invoice.editing;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record EditInvoiceResult(Invoice invoice) {
  public EditInvoiceResult {
    Objects.requireNonNull(invoice, "invoice cannot be null");
  }
}
