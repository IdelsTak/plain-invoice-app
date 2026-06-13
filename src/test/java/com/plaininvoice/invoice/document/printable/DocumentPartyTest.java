package com.plaininvoice.invoice.document.printable;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentPartyTest {

  @Test
  void trimsName() {
    assertThat(new DocumentParty(" Seller ", "TAX", "a@b.test").name(), is("Seller"));
  }

  @Test
  void trimsTaxId() {
    assertThat(new DocumentParty("Seller", " TAX ", "a@b.test").taxId(), is("TAX"));
  }

  @Test
  void trimsEmail() {
    assertThat(new DocumentParty("Seller", "TAX", " a@b.test ").email(), is("a@b.test"));
  }

  @Test
  void rejectsBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new DocumentParty(" ", "TAX", "a@b.test"));
  }
}
