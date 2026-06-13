package com.plaininvoice.invoice.document.pagination;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class PagePortTest {

  @Test
  void paginateLayoutIsPort() {
    assertThat(new PaginateLayout(), instanceOf(PagePort.class));
  }
}
