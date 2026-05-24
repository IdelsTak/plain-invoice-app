package com.plaininvoice.invoice.totals;

import com.plaininvoice.invoice.lifecycle.*;
import java.util.*;

public record ComputeTotalsRequest(Invoice invoice) {
  public ComputeTotalsRequest {
    Objects.requireNonNull(invoice, "invoice cannot be null");
  }
}
