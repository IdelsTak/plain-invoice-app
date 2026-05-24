package com.plaininvoice.invoice.pricing;

import java.util.*;

public record CurrencyCode(String value) {
  public CurrencyCode {
    Objects.requireNonNull(value, "currency code cannot be null");
    value = value.trim().toUpperCase(Locale.ROOT);
    if (!value.matches("[A-Z]{3}")) {
      throw new IllegalArgumentException("currency code must be a 3-letter ISO code");
    }
    Currency.getInstance(value);
  }

  public Currency currency() {
    return Currency.getInstance(value);
  }
}
