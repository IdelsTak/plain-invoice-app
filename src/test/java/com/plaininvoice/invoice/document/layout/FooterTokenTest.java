package com.plaininvoice.invoice.document.layout;

import com.plaininvoice.invoice.document.printable.*;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class FooterTokenTest {

  @Test
  void trimsText() {
    assertThat(new FooterToken(" Invoice ").text(), is("Invoice"));
  }

  @Test
  void allowsEmptyText() {
    assertThat(new FooterToken(" ").text(), is(""));
  }

  @Test
  void rejectsNullText() {
    assertThrows(NullPointerException.class, () -> new FooterToken(null));
  }
}
