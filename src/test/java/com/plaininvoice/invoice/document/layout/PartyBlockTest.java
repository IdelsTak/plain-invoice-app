package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PartyBlockTest {

  @Test
  void keepsSeller() {
    var seller = LayoutSamples.party("seller");
    assertThat(new PartyBlock(seller, LayoutSamples.party("buyer")).seller(), is(seller));
  }

  @Test
  void keepsBuyer() {
    var buyer = LayoutSamples.party("buyer");
    assertThat(new PartyBlock(LayoutSamples.party("seller"), buyer).buyer(), is(buyer));
  }

  @Test
  void rejectsNullSeller() {
    assertThrows(NullPointerException.class, () -> new PartyBlock(null, LayoutSamples.party("buyer")));
  }

  @Test
  void rejectsNullBuyer() {
    assertThrows(NullPointerException.class, () -> new PartyBlock(LayoutSamples.party("seller"), null));
  }
}
