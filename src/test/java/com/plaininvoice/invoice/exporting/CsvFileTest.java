package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class CsvFileTest {

  @Test
  void keepsValue() {
    assertThat(new CsvFile("a\r\n", StandardCharsets.UTF_8).value(), is("a\r\n"));
  }

  @Test
  void keepsCharset() {
    assertThat(new CsvFile("a\r\n", StandardCharsets.UTF_8).charset(), is(StandardCharsets.UTF_8));
  }

  @Test
  void rejectsEmptyValue() {
    assertThrows(IllegalArgumentException.class, () -> new CsvFile("", StandardCharsets.UTF_8));
  }

  @Test
  void rejectsNullCharset() {
    assertThrows(NullPointerException.class, () -> new CsvFile("a\r\n", null));
  }
}
