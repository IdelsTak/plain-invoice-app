package com.plaininvoice.invoice.storage.sqlite;

import com.plaininvoice.invoice.storage.*;

import java.util.*;

public record InvoiceStateRow(String code, Optional<VoidMark> voidMark) {
  public InvoiceStateRow {
    Objects.requireNonNull(code, "invoice state code cannot be null");
    code = code.trim().toUpperCase(Locale.ROOT);
    voidMark = Objects.requireNonNull(voidMark, "void mark cannot be null");
  }
}
