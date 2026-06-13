package com.plaininvoice.invoice.document.printable;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentPartiesTest {

  @Test
  void keepsSeller() {
    var seller = DocumentSamples.party("Seller");
    assertThat(new DocumentParties(seller, DocumentSamples.party("Buyer")).seller(), is(seller));
  }

  @Test
  void keepsBuyer() {
    var buyer = DocumentSamples.party("Buyer");
    assertThat(new DocumentParties(DocumentSamples.party("Seller"), buyer).buyer(), is(buyer));
  }

  @Test
  void rejectsNullSeller() {
    assertThrows(NullPointerException.class, () -> new DocumentParties(null, DocumentSamples.party("Buyer")));
  }

  @Test
  void rejectsNullBuyer() {
    assertThrows(NullPointerException.class, () -> new DocumentParties(DocumentSamples.party("Seller"), null));
  }
}
