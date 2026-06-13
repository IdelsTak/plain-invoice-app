package com.plaininvoice.invoice.numbering;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class MemoryInvoiceNumberUniquenessTest {

  @Test
  void rejectsAlreadySeenNumber() {
    var uniqueness = new MemoryInvoiceNumberUniqueness(new HashSet<>());
    uniqueness.verify(new InvoiceNumber("CORE", 1));
    assertThrows(IllegalArgumentException.class, () -> uniqueness.verify(new InvoiceNumber("CORE", 1)));
  }

  @Test
  void rejectsNullNumbers() {
    assertThrows(NullPointerException.class, () -> new MemoryInvoiceNumberUniqueness(null));
  }

  @Test
  void ignoresExternalMutation() {
    var numbers = new HashSet<InvoiceNumber>();
    var uniqueness = new MemoryInvoiceNumberUniqueness(numbers);
    numbers.add(new InvoiceNumber("CORE", 1));
    assertDoesNotThrow(() -> uniqueness.verify(new InvoiceNumber("CORE", 1)));
  }
}
