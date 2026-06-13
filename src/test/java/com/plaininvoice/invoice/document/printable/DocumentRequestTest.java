package com.plaininvoice.invoice.document.printable;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentRequestTest {

  @Test
  void keepsInvoice() {
    var invoice = DocumentSamples.invoice();
    assertThat(new DocumentRequest(invoice, DocumentSamples.meta()).invoice(), is(invoice));
  }

  @Test
  void keepsMeta() {
    var meta = DocumentSamples.meta();
    assertThat(new DocumentRequest(DocumentSamples.invoice(), meta).meta(), is(meta));
  }

  @Test
  void rejectsNullInvoice() {
    assertThrows(NullPointerException.class, () -> new DocumentRequest(null, DocumentSamples.meta()));
  }

  @Test
  void rejectsNullMeta() {
    assertThrows(NullPointerException.class, () -> new DocumentRequest(DocumentSamples.invoice(), null));
  }
}
