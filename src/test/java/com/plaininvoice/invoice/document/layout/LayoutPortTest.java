package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class LayoutPortTest {

  @Test
  void buildLayoutIsPort() {
    assertThat(new BuildLayout(), instanceOf(LayoutPort.class));
  }
}
