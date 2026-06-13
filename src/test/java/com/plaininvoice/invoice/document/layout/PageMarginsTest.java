package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PageMarginsTest {

  @Test
  void keepsTop() {
    assertThat(new PageMargins(1, 2, 3, 4).top(), is(1));
  }

  @Test
  void rejectsNegativeTop() {
    assertThrows(IllegalArgumentException.class, () -> new PageMargins(-1, 0, 0, 0));
  }

  @Test
  void rejectsNegativeRight() {
    assertThrows(IllegalArgumentException.class, () -> new PageMargins(0, -1, 0, 0));
  }

  @Test
  void rejectsNegativeBottom() {
    assertThrows(IllegalArgumentException.class, () -> new PageMargins(0, 0, -1, 0));
  }

  @Test
  void rejectsNegativeLeft() {
    assertThrows(IllegalArgumentException.class, () -> new PageMargins(0, 0, 0, -1));
  }
}
