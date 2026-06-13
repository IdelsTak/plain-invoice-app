package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class GoldenTextTest {

  @Test
  void normalizesCrLf() {
    assertThat(new GoldenText("a\nb").compare("a\r\nb").matched(), is(true));
  }

  @Test
  void stripsTrailingSpace() {
    assertThat(new GoldenText("a").compare("a  ").matched(), is(true));
  }

  @Test
  void keepsInnerSpace() {
    assertThat(new GoldenText("a  b").compare("a b").matched(), is(false));
  }

  @Test
  void exposesExpected() {
    assertThat(new GoldenText("a").compare("b").expected(), is("a"));
  }

  @Test
  void exposesActual() {
    assertThat(new GoldenText("a").compare("b").actual(), is("b"));
  }

  @Test
  void rejectsNullActual() {
    assertThrows(NullPointerException.class, () -> new GoldenText("a").compare(null));
  }
}
