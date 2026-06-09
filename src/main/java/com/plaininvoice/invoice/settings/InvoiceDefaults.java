package com.plaininvoice.invoice.settings;

import com.plaininvoice.invoice.numbering.*;
import com.plaininvoice.invoice.pricing.*;
import java.util.*;

public record InvoiceDefaults(CurrencyCode currency, PaymentDefault payment, String series, List<TaxPreset> taxes) {
  public InvoiceDefaults {
    Objects.requireNonNull(currency, "default currency cannot be null");
    Objects.requireNonNull(payment, "default payment cannot be null");
    Objects.requireNonNull(series, "default invoice series cannot be null");
    Objects.requireNonNull(taxes, "default tax presets cannot be null");
    series = series.trim().toUpperCase(Locale.ROOT);
    if (!series.matches("[A-Z0-9]{2,12}")) {
      throw new IllegalArgumentException("default invoice series must be 2-12 uppercase alphanumeric chars");
    }
    taxes = List.copyOf(taxes);
  }

  public InvoiceNumber number(long sequence) {
    return new InvoiceNumber(series, sequence);
  }
}
