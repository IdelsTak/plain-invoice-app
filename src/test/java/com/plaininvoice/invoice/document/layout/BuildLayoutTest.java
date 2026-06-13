package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class BuildLayoutTest {

  @Test
  void keepsMetadata() {
    var layout = layout();
    assertThat(layout.meta(), is(DocumentSamples.meta()));
  }

  @Test
  void usesA4Page() {
    var layout = layout();
    assertThat(layout.page().size(), is("A4"));
  }

  @Test
  void usesPortraitPage() {
    var layout = layout();
    assertThat(layout.page().orientation(), is("portrait"));
  }

  @Test
  void mapsHeaderNumber() {
    var layout = layout();
    assertThat(layout.header().number(), is("INV-1000"));
  }

  @Test
  void mapsSellerRole() {
    var layout = layout();
    assertThat(layout.parties().seller().role(), is("seller"));
  }

  @Test
  void mapsBuyerName() {
    var layout = layout();
    assertThat(layout.parties().buyer().party().name(), is("Buyer LLC"));
  }

  @Test
  void mapsLineCount() {
    var layout = layout();
    assertThat(layout.lines().lines(), hasSize(2));
  }

  @Test
  void mapsLineBreakHint() {
    var layout = layout();
    assertThat(layout.lines().lines().getFirst().breakHint().inside(), is("avoid"));
  }

  @Test
  void mapsTotals() {
    var layout = layout();
    assertThat(layout.totals().totalDue().amount(), comparesEqualTo(new BigDecimal("29.00")));
  }

  @Test
  void mapsTerms() {
    var layout = layout();
    assertThat(layout.terms().note(), is("Net 30"));
  }

  @Test
  void mapsFooter() {
    var layout = layout();
    assertThat(layout.footer().text(), is("Invoice INV-1000"));
  }

  @Test
  void mapsPageHints() {
    var layout = layout();
    assertThat(layout.hints().footerMode(), is("running"));
  }

  @Test
  void buildsDeterministicLayout() {
    var port = new BuildLayout();
    var document = LayoutSamples.document();
    assertThat(port.layout(document), is(port.layout(document)));
  }

  @Test
  void rejectsNullDocument() {
    var port = new BuildLayout();
    assertThrows(NullPointerException.class, () -> port.layout(null));
  }

  private LayoutDocument layout() {
    var port = new BuildLayout();
    return port.layout(LayoutSamples.document());
  }
}
