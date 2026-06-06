package com.plaininvoice.invoice.storage;

import java.util.*;

public record MoneyParts(long amountMinor, String currencyCode) {
  public MoneyParts {
    Objects.requireNonNull(currencyCode, "currency code cannot be null");
    currencyCode = currencyCode.trim().toUpperCase(Locale.ROOT);
  }
}
