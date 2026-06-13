package com.plaininvoice.invoice.document.pagination;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PageFrameTest {

  @Test
  void keepsNumber() {
    assertThat(PageSamples.frame().number(), is(1));
  }

  @Test
  void keepsBody() {
    var body = PageSamples.body(true);
    assertThat(new PageFrame(1, PageSamples.layout().header(), body, PageSamples.layout().footer()).body(), is(body));
  }

  @Test
  void rejectsZeroNumber() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new PageFrame(0, PageSamples.layout().header(), PageSamples.body(true), PageSamples.layout().footer())
    );
  }
}
