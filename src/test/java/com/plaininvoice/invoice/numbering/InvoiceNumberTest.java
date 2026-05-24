package com.plaininvoice.invoice.numbering;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceNumberTest {

  @Test
  void formatsSeriesAndSequence() {
    var number = new InvoiceNumber("core", 42);
    assertThat(number.formatted(), is("CORE-00042"));
  }

  @Test
  void rejectsInvalidSeries() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("x", 1));
  }

  @Test
  void rejectsNonPositiveSequence() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("CORE", 0));
  }
}
