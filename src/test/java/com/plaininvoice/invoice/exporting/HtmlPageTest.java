package com.plaininvoice.invoice.exporting;

import java.nio.charset.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class HtmlPageTest {

  @Test
  void keepsValue() {
    assertThat(new HtmlPage("<p>x</p>", StandardCharsets.UTF_8).value(), is("<p>x</p>"));
  }

  @Test
  void keepsCharset() {
    assertThat(new HtmlPage("<p>x</p>", StandardCharsets.UTF_8).charset(), is(StandardCharsets.UTF_8));
  }

  @Test
  void stripsTrailingSpace() {
    assertThat(new HtmlPage("<p>x</p>  ", StandardCharsets.UTF_8).value(), is("<p>x</p>"));
  }

  @Test
  void rejectsBlankValue() {
    assertThrows(IllegalArgumentException.class, () -> new HtmlPage(" ", StandardCharsets.UTF_8));
  }

  @Test
  void rejectsNullCharset() {
    assertThrows(NullPointerException.class, () -> new HtmlPage("<p>x</p>", null));
  }
}
