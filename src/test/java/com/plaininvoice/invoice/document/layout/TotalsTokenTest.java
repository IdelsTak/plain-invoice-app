package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class TotalsTokenTest {

  @Test
  void keepsSubtotal() {
    var money = DocumentSamples.money("20.00");
    assertThat(new TotalsToken(money, DocumentSamples.money("3.20"), DocumentSamples.money("23.20")).subtotal(), is(money));
  }

  @Test
  void keepsTax() {
    var money = DocumentSamples.money("3.20");
    assertThat(new TotalsToken(DocumentSamples.money("20.00"), money, DocumentSamples.money("23.20")).tax(), is(money));
  }

  @Test
  void keepsTotalDue() {
    var money = DocumentSamples.money("23.20");
    assertThat(new TotalsToken(DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), money).totalDue(), is(money));
  }

  @Test
  void rejectsNullSubtotal() {
    assertThrows(NullPointerException.class, () -> new TotalsToken(null, DocumentSamples.money("3.20"), DocumentSamples.money("23.20")));
  }

  @Test
  void rejectsNullTax() {
    assertThrows(NullPointerException.class, () -> new TotalsToken(DocumentSamples.money("20.00"), null, DocumentSamples.money("23.20")));
  }

  @Test
  void rejectsNullTotalDue() {
    assertThrows(NullPointerException.class, () -> new TotalsToken(DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), null));
  }
}
