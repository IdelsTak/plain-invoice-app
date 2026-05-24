package com.plaininvoice.invoice.creation;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record CreateInvoiceResult(Invoice invoice) {
  public CreateInvoiceResult {
    Objects.requireNonNull(invoice, "invoice cannot be null");
  }
}
