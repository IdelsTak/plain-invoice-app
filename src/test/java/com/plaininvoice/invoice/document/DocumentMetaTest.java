package com.plaininvoice.invoice.document;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class DocumentMetaTest {

  @Test
  void trimsTitle() {
    assertThat(new DocumentMeta(" Invoice ", "en").title(), is("Invoice"));
  }

  @Test
  void trimsLanguage() {
    assertThat(new DocumentMeta("Invoice", " en ").language(), is("en"));
  }

  @Test
  void rejectsBlankTitle() {
    assertThrows(IllegalArgumentException.class, () -> new DocumentMeta(" ", "en"));
  }

  @Test
  void rejectsBlankLanguage() {
    assertThrows(IllegalArgumentException.class, () -> new DocumentMeta("Invoice", " "));
  }
}
