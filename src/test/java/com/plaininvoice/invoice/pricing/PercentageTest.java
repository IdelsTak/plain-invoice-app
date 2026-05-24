package com.plaininvoice.invoice.pricing;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class PercentageTest {

  @Test
  void rejectsPercentageAboveHundred() {
    assertThrows(IllegalArgumentException.class, () -> new Percentage(new BigDecimal("100.01")));
  }

  @Test
  void rejectsNegativePercentage() {
    assertThrows(IllegalArgumentException.class, () -> new Percentage(new BigDecimal("-0.01")));
  }
}
