package com.plaininvoice.invoice.numbering;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceNumberParserTest {

  @Test
  void parsesSeriesAndSequence() {
    var parser = new InvoiceNumberParser();
    var parsed = parser.parse("core-42");
    assertThat(parsed, is(new InvoiceNumber("CORE", 42)));
  }

  @Test
  void rejectsMalformedNumber() {
    var parser = new InvoiceNumberParser();
    assertThrows(IllegalArgumentException.class, () -> parser.parse("bad_format"));
  }
}
