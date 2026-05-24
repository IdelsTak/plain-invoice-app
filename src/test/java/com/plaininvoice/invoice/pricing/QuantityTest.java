package com.plaininvoice.invoice.pricing;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class QuantityTest {

  @Test
  void rejectsNegativeQuantity() {
    assertThrows(IllegalArgumentException.class, () -> new Quantity(new BigDecimal("-1")));
  }
}
