package com.plaininvoice.invoice.document;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceDocumentTest {

  @Test
  void keepsMeta() {
    var meta = DocumentSamples.meta();
    assertThat(document(meta, DocumentSamples.lines()).meta(), is(meta));
  }

  @Test
  void keepsHeader() {
    var header = DocumentSamples.header();
    assertThat(new InvoiceDocument(DocumentSamples.meta(), header, DocumentSamples.parties(), DocumentSamples.lines(), DocumentSamples.totals(), DocumentSamples.terms()).header(), is(header));
  }

  @Test
  void keepsParties() {
    var parties = DocumentSamples.parties();
    assertThat(new InvoiceDocument(DocumentSamples.meta(), DocumentSamples.header(), parties, DocumentSamples.lines(), DocumentSamples.totals(), DocumentSamples.terms()).parties(), is(parties));
  }

  @Test
  void keepsTotals() {
    var totals = DocumentSamples.totals();
    assertThat(new InvoiceDocument(DocumentSamples.meta(), DocumentSamples.header(), DocumentSamples.parties(), DocumentSamples.lines(), totals, DocumentSamples.terms()).totals(), is(totals));
  }

  @Test
  void keepsTerms() {
    var terms = DocumentSamples.terms();
    assertThat(new InvoiceDocument(DocumentSamples.meta(), DocumentSamples.header(), DocumentSamples.parties(), DocumentSamples.lines(), DocumentSamples.totals(), terms).terms(), is(terms));
  }

  @Test
  void copiesLines() {
    var lines = new ArrayList<>(DocumentSamples.lines());
    var document = document(DocumentSamples.meta(), lines);
    lines.clear();
    assertThat(document.lines(), hasSize(1));
  }

  @Test
  void returnsImmutableLines() {
    var document = document(DocumentSamples.meta(), DocumentSamples.lines());
    assertThrows(UnsupportedOperationException.class, () -> document.lines().clear());
  }

  @Test
  void rejectsEmptyLines() {
    assertThrows(IllegalArgumentException.class, () -> document(DocumentSamples.meta(), List.of()));
  }

  private InvoiceDocument document(DocumentMeta meta, List<DocumentLine> lines) {
    return new InvoiceDocument(
      meta,
      DocumentSamples.header(),
      DocumentSamples.parties(),
      lines,
      DocumentSamples.totals(),
      DocumentSamples.terms()
    );
  }
}
