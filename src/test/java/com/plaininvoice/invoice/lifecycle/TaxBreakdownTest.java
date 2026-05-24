package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class TaxBreakdownTest {

  @Test
  void rejectsMismatchedTaxBreakdown() {
    var base = new Money(new BigDecimal("100.00"), new CurrencyCode("USD"));
    var invalidTax = new Money(new BigDecimal("10.00"), new CurrencyCode("USD"));

    assertThrows(
      IllegalArgumentException.class,
      () -> new TaxBreakdown(new Percentage(new BigDecimal("16")), base, invalidTax)
    );
  }
}
