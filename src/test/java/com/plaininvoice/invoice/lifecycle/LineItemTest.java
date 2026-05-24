package com.plaininvoice.invoice.lifecycle;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class LineItemTest {

  @Test
  void rejectsBlankDescription() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new LineItem(
        "  ",
        new Quantity(new BigDecimal("1")),
        new Money(new BigDecimal("10.00"), new CurrencyCode("USD")),
        new Percentage(new BigDecimal("16"))
      )
    );
  }

  @Test
  void computesSubtotal() {
    var line = sampleLineItem();
    assertThat(line.subtotal().amount(), comparesEqualTo(new BigDecimal("20.00")));
  }

  @Test
  void computesTaxAmount() {
    var line = sampleLineItem();
    assertThat(line.tax().tax().amount(), comparesEqualTo(new BigDecimal("3.20")));
  }

  @Test
  void computesTotal() {
    var line = sampleLineItem();
    assertThat(line.total().amount(), comparesEqualTo(new BigDecimal("23.20")));
  }

  private LineItem sampleLineItem() {
    return new LineItem(
      "Service",
      new Quantity(new BigDecimal("2")),
      new Money(new BigDecimal("10.00"), new CurrencyCode("USD")),
      new Percentage(new BigDecimal("16"))
    );
  }
}
