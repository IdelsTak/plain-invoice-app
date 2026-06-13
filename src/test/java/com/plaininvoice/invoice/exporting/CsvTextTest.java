package com.plaininvoice.invoice.exporting;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class CsvTextTest {

  @Test
  void keepsPlainField() {
    assertThat(CsvText.field("abc"), is("abc"));
  }

  @Test
  void quotesComma() {
    assertThat(CsvText.field("a,b"), is("\"a,b\""));
  }

  @Test
  void doublesQuote() {
    assertThat(CsvText.field("a\"b"), is("\"a\"\"b\""));
  }

  @Test
  void quotesNewline() {
    assertThat(CsvText.field("a\nb"), is("\"a\nb\""));
  }

  @Test
  void quotesCarriageReturn() {
    assertThat(CsvText.field("a\rb"), is("\"a\rb\""));
  }

  @Test
  void quotesLeadingSpace() {
    assertThat(CsvText.field(" a"), is("\" a\""));
  }

  @Test
  void quotesTrailingSpace() {
    assertThat(CsvText.field("a "), is("\"a \""));
  }

  @Test
  void rejectsNullField() {
    assertThrows(NullPointerException.class, () -> CsvText.field(null));
  }
}
