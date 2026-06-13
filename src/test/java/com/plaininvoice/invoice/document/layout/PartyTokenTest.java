package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class PartyTokenTest {

  @Test
  void trimsRole() {
    assertThat(new PartyToken(" seller ", DocumentSamples.party("Seller")).role(), is("seller"));
  }

  @Test
  void keepsParty() {
    var party = DocumentSamples.party("Seller");
    assertThat(new PartyToken("seller", party).party(), is(party));
  }

  @Test
  void rejectsBlankRole() {
    assertThrows(IllegalArgumentException.class, () -> new PartyToken(" ", DocumentSamples.party("Seller")));
  }
}
