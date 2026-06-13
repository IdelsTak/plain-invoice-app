package com.plaininvoice.invoice.document.printable;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BuildDocumentTest {

  @Test
  void keepsMetadata() {
    var document = build();
    assertThat(document.meta(), is(DocumentSamples.meta()));
  }

  @Test
  void mapsHeaderNumber() {
    var document = build();
    assertThat(document.header().number(), is("INV-1000"));
  }

  @Test
  void mapsHeaderState() {
    var document = build();
    assertThat(document.header().state(), is("Draft"));
  }

  @Test
  void mapsSeller() {
    var document = build();
    assertThat(document.parties().seller().name(), is("Seller Ltd"));
  }

  @Test
  void mapsBuyer() {
    var document = build();
    assertThat(document.parties().buyer().name(), is("Buyer LLC"));
  }

  @Test
  void mapsLineCount() {
    var document = build();
    assertThat(document.lines(), hasSize(2));
  }

  @Test
  void mapsLinePosition() {
    var document = build();
    assertThat(document.lines().getFirst().position(), is(1));
  }

  @Test
  void mapsLineDescription() {
    var document = build();
    assertThat(document.lines().getFirst().description(), is("Service A"));
  }

  @Test
  void mapsLineTotal() {
    var document = build();
    assertThat(document.lines().getFirst().amounts().total().amount(), comparesEqualTo(new BigDecimal("23.20")));
  }

  @Test
  void mapsTotals() {
    var document = build();
    assertThat(document.totals().totalDue().amount(), comparesEqualTo(new BigDecimal("29.00")));
  }

  @Test
  void mapsTerms() {
    var document = build();
    assertThat(document.terms().note(), is("Net 30"));
  }

  @Test
  void buildsDeterministicDocument() {
    var port = new BuildDocument();
    var request = new DocumentRequest(DocumentSamples.invoice(), DocumentSamples.meta());
    assertThat(port.document(request), is(port.document(request)));
  }

  @Test
  void rejectsNullRequest() {
    var port = new BuildDocument();
    assertThrows(NullPointerException.class, () -> port.document(null));
  }

  private InvoiceDocument build() {
    var port = new BuildDocument();
    return port.document(new DocumentRequest(DocumentSamples.invoice(), DocumentSamples.meta()));
  }
}
