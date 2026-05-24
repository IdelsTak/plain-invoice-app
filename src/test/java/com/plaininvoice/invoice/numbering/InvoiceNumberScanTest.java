package com.plaininvoice.invoice.numbering;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceNumberScanTest {

  @Test
  void parsesSeriesAndSequence() {
    var scan = new InvoiceNumberScan();
    var parsed = scan.parse("core-42");
    assertThat(parsed, is(new InvoiceNumber("CORE", 42)));
  }

  @Test
  void rejectsMalformedNumber() {
    var scan = new InvoiceNumberScan();
    assertThrows(IllegalArgumentException.class, () -> scan.parse("bad_format"));
  }
}
