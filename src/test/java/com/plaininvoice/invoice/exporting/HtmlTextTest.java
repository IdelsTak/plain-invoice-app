package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class HtmlTextTest {

  @Test
  void escapesAmpersand() {
    assertThat(HtmlText.escaped("A & B"), is("A &amp; B"));
  }

  @Test
  void escapesLessThan() {
    assertThat(HtmlText.escaped("<tag"), is("&lt;tag"));
  }

  @Test
  void escapesGreaterThan() {
    assertThat(HtmlText.escaped("tag>"), is("tag&gt;"));
  }

  @Test
  void escapesQuote() {
    assertThat(HtmlText.escaped("\"x\""), is("&quot;x&quot;"));
  }

  @Test
  void escapesApostrophe() {
    assertThat(HtmlText.escaped("it's"), is("it&#39;s"));
  }

  @Test
  void rejectsNullText() {
    assertThrows(NullPointerException.class, () -> HtmlText.escaped(null));
  }
}
