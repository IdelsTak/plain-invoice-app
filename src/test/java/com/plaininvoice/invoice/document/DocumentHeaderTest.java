package com.plaininvoice.invoice.document;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentHeaderTest {

  @Test
  void trimsNumber() {
    assertThat(header(" INV-1 ", "Draft").number(), is("INV-1"));
  }

  @Test
  void trimsState() {
    assertThat(header("INV-1", " Draft ").state(), is("Draft"));
  }

  @Test
  void rejectsBlankNumber() {
    assertThrows(IllegalArgumentException.class, () -> header(" ", "Draft"));
  }

  @Test
  void rejectsBlankState() {
    assertThrows(IllegalArgumentException.class, () -> header("INV-1", " "));
  }

  private DocumentHeader header(String number, String state) {
    return new DocumentHeader(number, LocalDate.of(2026, 5, 24), state);
  }
}
