package com.plaininvoice.invoice.settings;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plaininvoice.invoice.pricing.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;

final class InvoiceDefaultsTest {

  @Test
  void rejectsNullCurrency() {
    assertThrows(NullPointerException.class, () -> new InvoiceDefaults(null, payment(), "CORE", taxes()));
  }

  @Test
  void rejectsNullPayment() {
    assertThrows(NullPointerException.class, () -> new InvoiceDefaults(currency(), null, "CORE", taxes()));
  }

  @Test
  void rejectsNullSeries() {
    assertThrows(NullPointerException.class, () -> new InvoiceDefaults(currency(), payment(), null, taxes()));
  }

  @Test
  void rejectsNullTaxes() {
    assertThrows(NullPointerException.class, () -> new InvoiceDefaults(currency(), payment(), "CORE", null));
  }

  @Test
  void rejectsShortSeries() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceDefaults(currency(), payment(), "A", taxes()));
  }

  @Test
  void uppercasesSeries() {
    assertThat(new InvoiceDefaults(currency(), payment(), "core", taxes()).series(), is("CORE"));
  }

  @Test
  void copiesTaxes() {
    var taxes = new ArrayList<>(taxes());
    var defaults = new InvoiceDefaults(currency(), payment(), "CORE", taxes);
    taxes.clear();
    assertThat(defaults.taxes().size(), is(1));
  }

  @Test
  void makesTaxesImmutable() {
    var defaults = new InvoiceDefaults(currency(), payment(), "CORE", taxes());
    assertThrows(UnsupportedOperationException.class, () -> defaults.taxes().add(tax()));
  }

  @Test
  void createsInvoiceNumber() {
    assertThat(new InvoiceDefaults(currency(), payment(), "CORE", taxes()).number(7).formatted(), is("CORE-00007"));
  }

  @Test
  void rejectsInvalidSequence() {
    assertThrows(IllegalArgumentException.class, () -> new InvoiceDefaults(currency(), payment(), "CORE", taxes()).number(0));
  }

  private CurrencyCode currency() {
    return new CurrencyCode("USD");
  }

  private PaymentDefault payment() {
    return new PaymentDefault(30, "Net 30");
  }

  private List<TaxPreset> taxes() {
    return List.of(tax());
  }

  private TaxPreset tax() {
    return new TaxPreset("VAT", new Percentage(new BigDecimal("16")));
  }
}
