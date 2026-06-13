package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceTaxRow(InvoiceTaxKey key, long rateBps, MoneyParts base, MoneyParts amount) {
  public InvoiceTaxRow {
    Objects.requireNonNull(key, "invoice tax key cannot be null");
    Objects.requireNonNull(base, "invoice tax base cannot be null");
    Objects.requireNonNull(amount, "invoice tax amount cannot be null");
    if (rateBps < 0) {
      throw new IllegalArgumentException("invoice tax rate cannot be negative");
    }
  }
}
