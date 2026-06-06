package com.plaininvoice.invoice.storage;

import java.util.*;

public record InvoiceStoreMeta(InvoiceStoreKey key, StoreClock clock, Optional<VoidMark> voidMark) {
  public InvoiceStoreMeta {
    Objects.requireNonNull(key, "invoice storage key cannot be null");
    Objects.requireNonNull(clock, "invoice storage clock cannot be null");
    voidMark = Objects.requireNonNull(voidMark, "void mark cannot be null");
  }
}
