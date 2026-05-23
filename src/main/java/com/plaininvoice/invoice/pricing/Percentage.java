package com.plaininvoice.invoice.pricing;

import java.math.*;
import java.util.*;

public record Percentage(BigDecimal value) {
  public Percentage {
    Objects.requireNonNull(value, "percentage cannot be null");
    if (value.signum() < 0 || value.compareTo(new BigDecimal("100")) > 0) {
      throw new IllegalArgumentException("percentage must be between 0 and 100");
    }
    value = value.stripTrailingZeros();
  }

  public BigDecimal factor() {
    return value.movePointLeft(2);
  }
}
