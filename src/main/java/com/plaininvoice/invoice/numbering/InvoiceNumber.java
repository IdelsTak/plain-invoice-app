package com.plaininvoice.invoice.numbering;

import java.util.*;

public record InvoiceNumber(String series, long sequence) {
  public InvoiceNumber {
    Objects.requireNonNull(series, "invoice series cannot be null");
    series = series.trim().toUpperCase(Locale.ROOT);
    if (!series.matches("[A-Z0-9]{2,12}")) {
      throw new IllegalArgumentException("invoice series must be 2-12 uppercase alphanumeric chars");
    }
    if (sequence <= 0) {
      throw new IllegalArgumentException("invoice sequence must be positive");
    }
  }

  public String formatted() {
    return "%s-%05d".formatted(series, sequence);
  }
}
