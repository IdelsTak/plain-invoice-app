package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PageHintsTest {

  @Test
  void trimsHeaderMode() {
    assertThat(new PageHints(" running ", "running", LayoutSamples.breakHint()).headerMode(), is("running"));
  }

  @Test
  void trimsFooterMode() {
    assertThat(new PageHints("running", " running ", LayoutSamples.breakHint()).footerMode(), is("running"));
  }

  @Test
  void keepsTableBreak() {
    var hint = LayoutSamples.breakHint();
    assertThat(new PageHints("running", "running", hint).tableBreak(), is(hint));
  }

  @Test
  void rejectsBlankHeaderMode() {
    assertThrows(IllegalArgumentException.class, () -> new PageHints(" ", "running", LayoutSamples.breakHint()));
  }

  @Test
  void rejectsBlankFooterMode() {
    assertThrows(IllegalArgumentException.class, () -> new PageHints("running", " ", LayoutSamples.breakHint()));
  }
}
