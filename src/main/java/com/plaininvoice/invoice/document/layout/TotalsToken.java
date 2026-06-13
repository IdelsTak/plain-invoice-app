package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.pricing.*;
import com.plaininvoice.invoice.document.printable.*;
import java.util.*;

public record TotalsToken(Money subtotal, Money tax, Money totalDue) {
  public TotalsToken {
    Objects.requireNonNull(subtotal, "subtotal cannot be null");
    Objects.requireNonNull(tax, "tax cannot be null");
    Objects.requireNonNull(totalDue, "total due cannot be null");
  }
}
