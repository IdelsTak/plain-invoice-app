package com.plaininvoice.invoice.document;

import java.math.*;
import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentLineTest {

  @Test
  void keepsPosition() {
    assertThat(line(1, "Service").position(), is(1));
  }

  @Test
  void trimsDescription() {
    assertThat(line(1, " Service ").description(), is("Service"));
  }

  @Test
  void keepsQuantity() {
    var quantity = DocumentSamples.quantity("2");
    assertThat(new DocumentLine(1, "Service", quantity, DocumentSamples.percent("16"), DocumentSamples.amounts()).quantity(), is(quantity));
  }

  @Test
  void keepsTaxRate() {
    var taxRate = DocumentSamples.percent("16");
    assertThat(new DocumentLine(1, "Service", DocumentSamples.quantity("2"), taxRate, DocumentSamples.amounts()).taxRate(), is(taxRate));
  }

  @Test
  void keepsAmounts() {
    var amounts = DocumentSamples.amounts();
    assertThat(new DocumentLine(1, "Service", DocumentSamples.quantity("2"), DocumentSamples.percent("16"), amounts).amounts(), is(amounts));
  }

  @Test
  void rejectsZeroPosition() {
    assertThrows(IllegalArgumentException.class, () -> line(0, "Service"));
  }

  @Test
  void rejectsBlankDescription() {
    assertThrows(IllegalArgumentException.class, () -> line(1, " "));
  }

  private DocumentLine line(int position, String description) {
    return new DocumentLine(
      position,
      description,
      DocumentSamples.quantity("2"),
      DocumentSamples.percent("16"),
      DocumentSamples.amounts()
    );
  }
}
