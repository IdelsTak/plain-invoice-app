package com.plaininvoice.invoice.document;

import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record DocumentAmounts(Money unitPrice, Money subtotal, Money tax, Money total) {
  public DocumentAmounts {
    Objects.requireNonNull(unitPrice, "unit price cannot be null");
    Objects.requireNonNull(subtotal, "line subtotal cannot be null");
    Objects.requireNonNull(tax, "line tax cannot be null");
    Objects.requireNonNull(total, "line total cannot be null");
  }
}
