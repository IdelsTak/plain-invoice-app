package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceHeadRow(InvoiceStoreMeta meta, String number, String currencyCode, InvoiceHeadData data) {
  public InvoiceHeadRow {
    Objects.requireNonNull(meta, "invoice storage metadata cannot be null");
    Objects.requireNonNull(number, "invoice number row cannot be null");
    Objects.requireNonNull(currencyCode, "invoice currency row cannot be null");
    Objects.requireNonNull(data, "invoice head data cannot be null");
    number = number.trim();
    currencyCode = currencyCode.trim().toUpperCase(Locale.ROOT);
  }
}
