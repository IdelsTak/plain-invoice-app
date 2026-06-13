package com.plaininvoice.invoice.document.printable;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record DocumentTotals(Money subtotal, Money tax, Money totalDue) {
  public DocumentTotals {
    Objects.requireNonNull(subtotal, "document subtotal cannot be null");
    Objects.requireNonNull(tax, "document tax cannot be null");
    Objects.requireNonNull(totalDue, "document total due cannot be null");
  }
}
