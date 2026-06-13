package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import java.time.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class HeaderTokenTest {

  @Test
  void trimsTitle() {
    assertThat(header(" Invoice ", "INV-1", "Draft").title(), is("Invoice"));
  }

  @Test
  void trimsNumber() {
    assertThat(header("Invoice", " INV-1 ", "Draft").number(), is("INV-1"));
  }

  @Test
  void trimsState() {
    assertThat(header("Invoice", "INV-1", " Draft ").state(), is("Draft"));
  }

  @Test
  void rejectsBlankTitle() {
    assertThrows(IllegalArgumentException.class, () -> header(" ", "INV-1", "Draft"));
  }

  @Test
  void rejectsBlankNumber() {
    assertThrows(IllegalArgumentException.class, () -> header("Invoice", " ", "Draft"));
  }

  @Test
  void rejectsBlankState() {
    assertThrows(IllegalArgumentException.class, () -> header("Invoice", "INV-1", " "));
  }

  private HeaderToken header(String title, String number, String state) {
    return new HeaderToken(title, number, LocalDate.of(2026, 5, 24), state);
  }
}
