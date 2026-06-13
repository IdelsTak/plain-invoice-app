package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class PdfPortTest {

  @Test
  void buildsPdf() {
    assertThat(new BuildPdf(), is(instanceOf(PdfPort.class)));
  }
}
