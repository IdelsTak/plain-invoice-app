package com.plaininvoice.invoice.numbering;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class InvoiceNumberTest {

  @Test
  void formatsSeriesAndSequence() {
    var number = new InvoiceNumber("core", 42);
    assertThat(number.formatted(), is("CORE-00042"));
  }

  @Test
  void rejectsInvalidSeries() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("x", 1));
  }

  @Test
  void rejectsNonPositiveSequence() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("CORE", 0));
  }

  @Test
  void acceptsTwoCharacterSeries() {
    var number = new InvoiceNumber("AB", 1);
    assertThat(number.formatted(), is("AB-00001"));
  }

  @Test
  void acceptsTwelveCharacterSeries() {
    var number = new InvoiceNumber("AB12CD34EF56", 7);
    assertThat(number.formatted(), is("AB12CD34EF56-00007"));
  }

  @Test
  void rejectsSeriesLongerThanTwelveCharacters() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("AB12CD34EF567", 1));
  }

  @Test
  void rejectsSeriesWithDashCharacter() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceNumber("AB-12", 1));
  }

  @Test
  void rejectsNullSeries() {
    assertThrows(NullPointerException.class, () -> new InvoiceNumber(null, 1));
  }
}
