package com.plaininvoice.invoice.document.printable;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentAmountsTest {

  @Test
  void keepsUnitPrice() {
    var money = DocumentSamples.money("10.00");
    assertThat(new DocumentAmounts(money, DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), DocumentSamples.money("23.20")).unitPrice(), is(money));
  }

  @Test
  void keepsSubtotal() {
    var money = DocumentSamples.money("20.00");
    assertThat(new DocumentAmounts(DocumentSamples.money("10.00"), money, DocumentSamples.money("3.20"), DocumentSamples.money("23.20")).subtotal(), is(money));
  }

  @Test
  void keepsTax() {
    var money = DocumentSamples.money("3.20");
    assertThat(new DocumentAmounts(DocumentSamples.money("10.00"), DocumentSamples.money("20.00"), money, DocumentSamples.money("23.20")).tax(), is(money));
  }

  @Test
  void keepsTotal() {
    var money = DocumentSamples.money("23.20");
    assertThat(new DocumentAmounts(DocumentSamples.money("10.00"), DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), money).total(), is(money));
  }

  @Test
  void rejectsNullUnitPrice() {
    assertThrows(NullPointerException.class, () -> new DocumentAmounts(null, DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), DocumentSamples.money("23.20")));
  }

  @Test
  void rejectsNullSubtotal() {
    assertThrows(NullPointerException.class, () -> new DocumentAmounts(DocumentSamples.money("10.00"), null, DocumentSamples.money("3.20"), DocumentSamples.money("23.20")));
  }

  @Test
  void rejectsNullTax() {
    assertThrows(NullPointerException.class, () -> new DocumentAmounts(DocumentSamples.money("10.00"), DocumentSamples.money("20.00"), null, DocumentSamples.money("23.20")));
  }

  @Test
  void rejectsNullTotal() {
    assertThrows(NullPointerException.class, () -> new DocumentAmounts(DocumentSamples.money("10.00"), DocumentSamples.money("20.00"), DocumentSamples.money("3.20"), null));
  }
}
