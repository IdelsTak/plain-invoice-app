package com.plaininvoice.invoice.document;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class DocumentPortTest {

  @Test
  void buildDocumentIsPort() {
    assertThat(new BuildDocument(), instanceOf(DocumentPort.class));
  }
}
