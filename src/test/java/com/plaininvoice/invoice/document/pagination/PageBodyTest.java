package com.plaininvoice.invoice.document.pagination;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class PageBodyTest {

  @Test
  void keepsFinalPage() {
    assertThat(PageSamples.body(true).finalPage(), is(true));
  }

  @Test
  void keepsNonFinalPage() {
    assertThat(PageSamples.body(false).finalPage(), is(false));
  }
}
