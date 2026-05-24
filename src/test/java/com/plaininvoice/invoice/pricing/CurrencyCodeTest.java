package com.plaininvoice.invoice.pricing;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class CurrencyCodeTest {

  @Test
  void rejectsInvalidCurrencyCode() {
    assertThrows(IllegalArgumentException.class, () -> new CurrencyCode("US"));
  }
}
