package com.plaininvoice.invoice.pricing;

import java.math.*;
import java.util.*;

public final class MonetaryArithmeticPolicy {
  private final RoundingMode roundingMode;

  public MonetaryArithmeticPolicy() {
    this(RoundingMode.HALF_UP);
  }

  public MonetaryArithmeticPolicy(RoundingMode roundingMode) {
    this.roundingMode = Objects.requireNonNull(roundingMode, "rounding mode cannot be null");
  }

  public BigDecimal normalize(BigDecimal amount, CurrencyCode currencyCode) {
    Objects.requireNonNull(amount, "amount cannot be null");
    Objects.requireNonNull(currencyCode, "currency code cannot be null");
    var scale = currencyCode.currency().getDefaultFractionDigits();
    if (scale < 0) {
      throw new IllegalArgumentException("currency must have a defined minor unit");
    }
    return amount.setScale(scale, roundingMode);
  }

  public RoundingMode roundingMode() {
    return roundingMode;
  }
}
