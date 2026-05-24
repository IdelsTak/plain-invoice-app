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

  @Test
  void rejectsNullValue() {
    var scan = new InvoiceNumberScan();
    assertThrows(NullPointerException.class, () -> scan.parse(null));
  }

  @Test
  void parsesWithBoundarySeriesLength() {
    var scan = new InvoiceNumberScan();
    var parsed = scan.parse("AB12CD34EF56-1");
    assertThat(parsed, is(new InvoiceNumber("AB12CD34EF56", 1)));
  }

  @Test
  void rejectsSequenceLongerThanTenDigits() {
    var scan = new InvoiceNumberScan();
    assertThrows(IllegalArgumentException.class, () -> scan.parse("CORE-12345678901"));
  }
}
