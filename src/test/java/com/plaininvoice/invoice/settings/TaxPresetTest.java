package com.plaininvoice.invoice.settings;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import org.junit.jupiter.api.*;

final class TaxPresetTest {

  @Test
  void rejectsNullLabel() {
    assertThrows(NullPointerException.class, () -> new TaxPreset(null, rate()));
  }

  @Test
  void rejectsNullRate() {
    assertThrows(NullPointerException.class, () -> new TaxPreset("VAT", null));
  }

  @Test
  void rejectsBlankLabel() {
    assertThrows(IllegalArgumentException.class, () -> new TaxPreset(" ", rate()));
  }

  @Test
  void trimsLabel() {
    assertThat(new TaxPreset(" VAT ", rate()).label(), is("VAT"));
  }

  @Test
  void keepsRate() {
    var rate = rate();
    assertThat(new TaxPreset("VAT", rate).rate(), is(rate));
  }

  private Percentage rate() {
    return new Percentage(new BigDecimal("16"));
  }
}
