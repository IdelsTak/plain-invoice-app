package com.plaininvoice.invoice.numbering;

import java.util.*;

public final class MemoryInvoiceNumberUniqueness implements InvoiceNumberUniqueness {
  private final Set<InvoiceNumber> numbers;

  public MemoryInvoiceNumberUniqueness(Set<InvoiceNumber> numbers) {
    this.numbers = new HashSet<>(Objects.requireNonNull(numbers, "numbers cannot be null"));
  }

  @Override
  public void verify(InvoiceNumber number) {
    Objects.requireNonNull(number, "invoice number cannot be null");
    if (!numbers.add(number)) {
      throw new IllegalArgumentException("invoice number must be unique");
    }
  }
}
