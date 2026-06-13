package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceRows(InvoiceHeadRow head, List<InvoiceLineRow> lines, List<InvoiceTaxRow> taxes) {
  public InvoiceRows {
    Objects.requireNonNull(head, "invoice head row cannot be null");
    Objects.requireNonNull(lines, "invoice line rows cannot be null");
    Objects.requireNonNull(taxes, "invoice tax rows cannot be null");
    lines = List.copyOf(lines);
    taxes = List.copyOf(taxes);
  }
}
