package com.plaininvoice.invoice.document.pagination;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PageRulesTest {

  @Test
  void keepsLinesPerPage() {
    assertThat(new PageRules(24).linesPerPage(), is(24));
  }

  @Test
  void rejectsZeroLinesPerPage() {
    assertThrows(IllegalArgumentException.class, () -> new PageRules(0));
  }
}
