package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class CsvPortTest {

  @Test
  void buildsCsv() {
    assertThat(new BuildCsv(), is(instanceOf(CsvPort.class)));
  }
}
