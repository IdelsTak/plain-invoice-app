package com.plaininvoice.invoice.settings;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.lifecycle.*;
import org.junit.jupiter.api.*;

final class SellerProfileTest {

  @Test
  void rejectsNullSeller() {
    assertThrows(NullPointerException.class, () -> new SellerProfile(null));
  }

  @Test
  void keepsSeller() {
    var seller = seller();
    assertThat(new SellerProfile(seller).seller(), is(seller));
  }

  private Party seller() {
    return new Party("Seller Ltd", "TAX-01", "seller@example.com");
  }
}
