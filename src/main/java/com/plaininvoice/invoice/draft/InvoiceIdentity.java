package com.plaininvoice.invoice.draft;

import java.util.*;

public record InvoiceIdentity(String number) {
  public InvoiceIdentity {
    Objects.requireNonNull(number, "invoice number cannot be null");
  }
}
