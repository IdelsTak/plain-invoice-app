package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

final class LayoutDocumentTest {

  @Test
  void keepsHeader() {
    var header = LayoutSamples.header();
    assertThat(document(header).header(), is(header));
  }

  @Test
  void keepsHints() {
    var hints = LayoutSamples.hints();
    assertThat(document(hints).hints(), is(hints));
  }

  private LayoutDocument document(HeaderToken header) {
    return new LayoutDocument(
      DocumentSamples.meta(),
      LayoutSamples.page(),
      header,
      LayoutSamples.parties(),
      LayoutSamples.lines(),
      LayoutSamples.totals(),
      LayoutSamples.terms(),
      LayoutSamples.footer(),
      LayoutSamples.hints()
    );
  }

  private LayoutDocument document(PageHints hints) {
    return new LayoutDocument(
      DocumentSamples.meta(),
      LayoutSamples.page(),
      LayoutSamples.header(),
      LayoutSamples.parties(),
      LayoutSamples.lines(),
      LayoutSamples.totals(),
      LayoutSamples.terms(),
      LayoutSamples.footer(),
      hints
    );
  }
}
