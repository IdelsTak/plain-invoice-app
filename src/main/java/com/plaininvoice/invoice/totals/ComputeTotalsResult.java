package com.plaininvoice.invoice.totals;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record ComputeTotalsResult(Money subtotal, Money totalTax, Money totalDue) {
  public ComputeTotalsResult {
    Objects.requireNonNull(subtotal, "subtotal cannot be null");
    Objects.requireNonNull(totalTax, "total tax cannot be null");
    Objects.requireNonNull(totalDue, "total due cannot be null");
  }
}
