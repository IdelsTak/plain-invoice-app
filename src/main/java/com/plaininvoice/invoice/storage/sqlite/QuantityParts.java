package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

public record QuantityParts(long numerator, long denominator) {
  public QuantityParts {
    if (denominator <= 0) {
      throw new IllegalArgumentException("quantity denominator must be positive");
    }
  }
}
