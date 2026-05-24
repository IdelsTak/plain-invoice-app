package com.plaininvoice.invoice.numbering;

import java.util.*;

public final class InvoiceNumberScan {

  public InvoiceNumber parse(String value) {
    Objects.requireNonNull(value, "invoice number cannot be null");
    var trimmed = value.trim();
    if (!trimmed.matches("[A-Za-z0-9]{2,12}-[0-9]{1,10}")) {
      throw new IllegalArgumentException("invoice number must match SERIES-SEQUENCE");
    }

    var split = trimmed.split("-", 2);
    var series = split[0];
    var sequence = Long.parseLong(split[1]);
    return new InvoiceNumber(series, sequence);
  }
}
