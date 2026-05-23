package com.plaininvoice.invoice.pricing;

import java.math.*;
import java.util.*;

public record Quantity(BigDecimal value) {
  public Quantity {
    Objects.requireNonNull(value, "quantity cannot be null");
    if (value.signum() < 0) {
      throw new IllegalArgumentException("quantity cannot be negative");
    }
    value = value.stripTrailingZeros();
  }
}
