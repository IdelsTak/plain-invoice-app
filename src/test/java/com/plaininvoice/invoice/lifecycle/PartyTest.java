package com.plaininvoice.invoice.lifecycle;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

final class PartyTest {

  @Test
  void rejectsBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new Party("  ", "TAX-01", "a@b.com"));
  }

  @Test
  void defaultsNullTaxIdToEmpty() {
    var party = new Party("Seller", null, "seller@example.com");
    assertEquals("", party.taxId());
  }

  @Test
  void defaultsNullEmailToEmpty() {
    var party = new Party("Seller", "TAX-01", null);
    assertEquals("", party.email());
  }
}
