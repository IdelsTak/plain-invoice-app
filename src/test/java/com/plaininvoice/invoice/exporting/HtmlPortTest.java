package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class HtmlPortTest {

  @Test
  void buildsHtml() {
    assertThat(new BuildHtml(), is(instanceOf(HtmlPort.class)));
  }
}
