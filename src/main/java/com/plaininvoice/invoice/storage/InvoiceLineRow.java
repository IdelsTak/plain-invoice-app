package com.plaininvoice.invoice.storage;

import java.util.*;

public record InvoiceLineRow(InvoiceLineKey key, String description, QuantityParts quantity, MoneyParts price) {
  public InvoiceLineRow {
    Objects.requireNonNull(key, "invoice line key cannot be null");
    Objects.requireNonNull(description, "invoice line description cannot be null");
    Objects.requireNonNull(quantity, "invoice line quantity cannot be null");
    Objects.requireNonNull(price, "invoice line price cannot be null");
    description = description.trim();
  }
}
